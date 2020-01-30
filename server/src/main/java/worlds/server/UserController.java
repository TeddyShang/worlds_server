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

}