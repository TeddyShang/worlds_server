package worlds.server;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class UserController {
    private final UserRepository userRepository;
    private final UserResourceAssembler userResourceAssembler;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileResourceAssembler userProfileResourceAssembler;
    private final BookingRepository bookingRepository;
    private final BookingResourceAssembler bookingResourceAssembler;

    UserController(UserRepository userRepository, UserResourceAssembler userResourceAssembler,
            UserProfileRepository userProfileRepository, UserProfileResourceAssembler userProfileResourceAssembler,
            BookingRepository bookingRepository, BookingResourceAssembler bookingResourceAssembler) {
        this.userRepository = userRepository;
        this.userResourceAssembler = userResourceAssembler;
        this.userProfileRepository = userProfileRepository;
        this.userProfileResourceAssembler = userProfileResourceAssembler;
        this.bookingRepository = bookingRepository;
        this.bookingResourceAssembler = bookingResourceAssembler;
    }

    /**
     * GET method
     * 
     * @return a list of all users
     */
    @ResponseBody
    @GetMapping(value = "/users", produces = "application/json; charset=UTF-8")
    Resources<Resource<UserProtected>> all() {
        List<User> userResults = userRepository.findAll();
        List<UserProtected> protectedResults = new ArrayList<>();
        for (User user : userResults) {
            UserProtected userProtected = new UserProtected();
            userProtected.convertFrom(user);
            protectedResults.add(userProtected);
        }

        List<Resource<UserProtected>> usersProtected = protectedResults.stream().map(userResourceAssembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(usersProtected, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }

    /**
     * GET method
     * 
     * @param id of a specific user
     * @return that specific user or UserNotFoundException if that id does not exist
     *         in the DB
     */
    @ResponseBody
    @GetMapping(value = "/users/{id}", produces = "application/json; charset=UTF-8")
    Resource<UserProtected> one(@PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        UserProtected userProtected = new UserProtected();
        userProtected.convertFrom(user);
        return userResourceAssembler.toResource(userProtected);
    }

    /**
     * GET method
     * 
     * @param id of a specific user
     * @return the profile of that user or UserNotFoundException if that id does not
     *         exist in the DB or UserProfileNotFoundException if that UserProfile
     *         does not exist NOTE: If the UserProfileNotFoundException is thrown,
     *         there are DB issues since a user should always have a corresponding
     *         user profile
     */
    @GetMapping(value = "/users/{id}/userprofile", produces = "application/json; charset=UTF-8")
    Resource<UserProfile> getProfile(@PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        String userProfileId = user.getProfileId();

        UserProfile userProfile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new UserProfileNotFoundException(userProfileId));

        return userProfileResourceAssembler.toResource(userProfile, user);
    }

    /**
     * GET method
     * 
     * @param id of a specific user
     * @return all bookings that "belong" to the user i.e. the realtor has booked or
     *         that a creator has accepepted or UserNotFoundException if the user
     *         does not exist or BookingNotFoundException if one of the bookingIds
     *         are not valid
     */
    @GetMapping(value = "/users/{id}/bookings", produces = "application/json; charset=UTF-8")
    Resources<Resource<Booking>> getBookings(@PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        String[] bookingIds = user.getBookingIds();
        List<Booking> bookingsResults = new ArrayList<Booking>();

        // if the user has no bookings or is empty, we should return an empty list
        if (bookingIds == null || bookingIds.length == 0) {
            List<Resource<Booking>> finalBookings = bookingsResults.stream().map(bookingResourceAssembler::toResource)
                    .collect(Collectors.toList());

            return new Resources<>(finalBookings,
                    linkTo(methodOn(UserController.class).getBookings(user.getId())).withSelfRel());

        }

        // otherwise find all the bookings and add them to the results list
        for (String bookingId : bookingIds) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException(bookingId));
            bookingsResults.add(booking);
        }

        List<Resource<Booking>> finalBookings = bookingsResults.stream().map(bookingResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(finalBookings,
                linkTo(methodOn(UserController.class).getBookings(user.getId())).withSelfRel());
    }

    /**
     * TODO: new control flow, constructors
     * POST method
     * 
     * @param newUser user to be created
     * @return URI to new user object
     * @throws URISyntaxException
     */
    @PostMapping("/users")
    ResponseEntity<?> newUser(@RequestBody User newUser) throws URISyntaxException {

        String firstName = newUser.getFirstName();
        String lastName = newUser.getLastName();

        // If the supplied UserType is not a designated Enum, the code will throw a 400
        UserType userType = newUser.getUserType();

        //Not a hashed password yet, just a field name, POST sends their plaintext password
        String password = newUser.getHashedPassword();

        String email = newUser.getEmail();

        // If any argument is null, we do not create the user object
        if (firstName == null || lastName == null || userType == null || password == null || email == null) {
            return ResponseEntity.badRequest().body("Bad Request: Null Arguments");
        }

        //TODO next check to see if the email is valid (REGEX)
        User checkSameEmail = userRepository.findByEmail(email);
        if (checkSameEmail != null) {
            return ResponseEntity.badRequest().body("Bad Request: Email already used");
        }


        //TODO next check to see if the password meets requirements, temporary char check right now (REGEX)
        if (password.length() < 8) {
            return ResponseEntity.badRequest().body("Bad Request: Password too weak");
        }

        // if the signup is for a realtor, they should have supplied a relator Id
        String realtorId = "";

        //chance for automatic approval
        Boolean autoApproved = false;
        if (userType == UserType.REALTOR) {
            realtorId = newUser.getRealtorId();

            if (realtorId == null) {
                return ResponseEntity.badRequest().body("Bad Request: Not a valid Realtor Id");
            }

            // verify realtor Id
            try {
                RealtorVerification realtorVerification = new RealtorVerification();
                Boolean isValidRealtor = realtorVerification.verifyUser(firstName, lastName, realtorId);
                autoApproved = isValidRealtor;

            } catch (IOException exception) {
                // unable to verify user but we carry on anyways, status is default to Pending
            }

        }
        
        //finally create the user
        User user = new User(firstName, lastName, userType, password, email, realtorId);

        //auto approval process for realtors
        if(autoApproved) {
            user.userStatus = UserStatus.APPROVED;
        }

        //create a profile for that user and save it
        UserProfile userProfile = new UserProfile();
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        user.setProfileId(savedUserProfile.getId());

        //save the user
        User savedUser = userRepository.save(user);

        //convert the user to a UserProtected, and return the UserProtected object
        UserProtected userProtected = new UserProtected();
        userProtected.convertFrom(savedUser);
        
        Resource<UserProtected> resource = userResourceAssembler.toResource(userProtected);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * TODO: Exception Checking
     * @param login Login object, username and unhashed password of user
     * @return ProtectedUser as Json if accepted, Unauthorized otherwise
     * @throws URISyntaxException
     */
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody Login login) throws URISyntaxException{
        Integer MAX_ATTEMPTS = 5;
        String email = login.getEmail();
        String password = login.getPassword();

        if(email == null || password == null) {
            return ResponseEntity.badRequest().body("Bad Request: Null Arguments");
        }
        User user = userRepository.findByEmail(email);

        if(user == null) {
            return new ResponseEntity<String>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }

        if (user.getFailedLogInAttempts() >= MAX_ATTEMPTS) {
            user.setUserState(UserState.LOCKED);
            userRepository.save(user);
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        String hashedPassword = user.getHashedPassword();
        Boolean passwordCorrect = BCrypt.checkpw(password, hashedPassword);
        if (!passwordCorrect) {
            Integer failedLogInAttempts = user.getFailedLogInAttempts();
            failedLogInAttempts++;
            user.setFailedLogInAttempts(failedLogInAttempts);
            userRepository.save(user);
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            user.setFailedLogInAttempts(0);
            userRepository.save(user);
            UserProtected userProtected = new UserProtected();
            userProtected.convertFrom(user);
            Resource<UserProtected> resource = userResourceAssembler.toResource(userProtected);
            return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
        }

    }
}