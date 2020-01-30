package  worlds.server;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    UserController(UserRepository repository, UserResourceAssembler assembler, UserProfileRepository userProfileRepository, UserProfileResourceAssembler userProfileResourceAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.userProfileRepository = userProfileRepository;
        this.userProfileResourceAssembler = userProfileResourceAssembler;
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

     /**
      * TODO: POST /users IVRE-181
      * When this call gets executed, we should validate the request and store a new user
      * object into the repository. We should also create a userProfile object and also store
      * that into the appropriate repository. 
      * It may be beneficial to save the userProfile first in order to obtain the profileId
      * to set for the user before saving
      */

}