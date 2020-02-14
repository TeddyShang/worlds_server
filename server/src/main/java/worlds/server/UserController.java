package worlds.server;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
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
    Resources<Resource<User>> all() {
        List<Resource<User>> users = userRepository.findAll().stream().map(userResourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(users, linkTo(methodOn(UserController.class).all()).withSelfRel());
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
    Resource<User> one(@PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userResourceAssembler.toResource(user);
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
        //If the supplied UserType is not a designated Enum, the code will throw a 400 BadRequest later
        UserType userType = newUser.getUserType();

        // If any argument is null, we do not create the user object
        if (firstName == null || lastName == null || userType == null) {
            return ResponseEntity.badRequest().body("Bad Request: Null Arguments");
        }

        User user = new User(firstName, lastName, userType);

        UserProfile userProfile = new UserProfile();
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        user.setProfileId(savedUserProfile.getId());

        Resource<User> resource = userResourceAssembler.toResource(userRepository.save(user));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

}