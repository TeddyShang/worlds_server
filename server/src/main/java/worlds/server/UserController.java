package worlds.server;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class UserController{
    private final UserRepository repository;
    private final UserResourceAssembler assembler;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileResourceAssembler userProfileResourceAssembler;
    private final BookingRepository bookingRepository;
    private final BookingResourceAssembler bookingResourceAssembler;

    UserController(UserRepository repository, UserResourceAssembler assembler, UserProfileRepository userProfileRepository, UserProfileResourceAssembler userProfileResourceAssembler, BookingRepository bookingRepository, BookingResourceAssembler bookingResourceAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.userProfileRepository = userProfileRepository;
        this.userProfileResourceAssembler = userProfileResourceAssembler;
        this.bookingRepository = bookingRepository;
        this.bookingResourceAssembler = bookingResourceAssembler;
    }

    @GetMapping(value = "/users", produces = "application/json; charset=UTF-8")
    Resources<Resource<User>> all() {
        List<Resource<User>> users = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());
        
        return new Resources<>(users,
            linkTo(methodOn(UserController.class).all()).withSelfRel()); 
    }

    @GetMapping(value = "/users/{id}", produces = "application/json; charset=UTF-8")
    Resource<User> one(@PathVariable String id) {
        User user = repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
        return assembler.toResource(user);
    }

    @GetMapping(value = "/users/{id}/userprofile", produces  = "application/json; charset=UTF-8")
    Resource<UserProfile> getProfile(@PathVariable String id) {
        User user = repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        String userProfileId = user.getProfileId();

        UserProfile userProfile = userProfileRepository.findById(userProfileId)
        .orElseThrow(() -> new UserProfileNotFoundException(userProfileId));

        return userProfileResourceAssembler.toResource(userProfile, user);
    }

    /**
     * TODO: GET /users/{id}/bookings IVRE-177
     * When this call gets executed, we should return a list of bookings
     * Follow the format of the GET calls above, and make sure to dependency inject an instance of the bookings repository
     * You should make this list HATEOAS complient, so you will need to modify BookingResourceAssembler.java and overload the toResource method
     * See UserProfileResourceAssembler.java for help and the above method (getProfile) as implementation will be very similar
     * To test: You will need to modify LoadDatabase.java. Create several booking objects and save several booking objects, and store their IDs into a list
     * Then set a user's bookingsID attribute to that list
     * Navigating to the URL should return all of those booking objects as well as a self-linked URL reference
     */

    @GetMapping(value = "/users/{id}/bookings", produces  = "application/json; charset=UTF-8")
    Resources<Resource<Booking>> getBookings(@PathVariable String id) {
        User user = repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        String[] bookingIds = user.getBookingIds();

        List<Booking> bookingsResults = new ArrayList<Booking>();

        for (String bookingId :bookingIds){
            Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
            bookingsResults.add(booking);
        }

        List<Resource<Booking>> finalBookings = bookingsResults.stream()
        .map(bookingResourceAssembler::toResource)
        .collect(Collectors.toList());

        return new Resources<>(finalBookings,
        linkTo(methodOn(UserController.class).getBookings(user.getId())).withSelfRel()); 
    }


    @PostMapping("/users")
    ResponseEntity<?> newUser (@RequestBody User newUser) throws URISyntaxException {

        User user = new User (newUser.getFirstName(), newUser.getLastName(), newUser.getUserType());

        UserProfile userProfile = new UserProfile();
        UserProfile temp = userProfileRepository.save(userProfile);
        user.setProfileId(temp.getId());

        Resource<User> resource = assembler.toResource(repository.save(user));
        
        return ResponseEntity
        .created(new URI(resource.getId().expand().getHref()))
        .body(resource);
    }
    
}