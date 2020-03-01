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
class UserProfileController{
    private final UserProfileRepository repository;
    private final UserProfileResourceAssembler assembler;

    UserProfileController(UserProfileRepository repository, UserProfileResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping(value = "/userprofiles", produces = "application/json; charset=UTF-8")
    Resources<Resource<UserProfile>> all() {
        List<Resource<UserProfile>> userProfiles = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());
        
        return new Resources<>(userProfiles,
            linkTo(methodOn(UserProfileController.class).all()).withSelfRel()); 
    }

    @GetMapping(value = "/userprofiles/{id}", produces = "application/json; charset=UTF-8")
    Resource<UserProfile> one(@PathVariable String id) {
        UserProfile userProfile = repository.findById(id)
        .orElseThrow(() -> new UserProfileNotFoundException(id));
        return assembler.toResource(userProfile);
    }

    @PutMapping("/userprofiles/{id}")
    ResponseEntity<UserProfile> updateUserProfile(@Valid @RequestBody UserProfile userProfileInfo,
    @PathVariable final String id) throws UserProfileNotFoundException {
        UserProfile userProfile = repository.findById(id)
        .orElseThrow(() -> new UserProfileNotFoundException("User profile does not exist with id ::" + id));

        userProfile.setAboutMe(userProfileInfo.getAboutMe());
        userProfile.setUrlToProfilePicture(userProfileInfo.getUrlToProfilePicture);
        userProfile.setProfessionalExperience(userProfileInfo.getProfessionalExperience);

        final UserProfile updatedUserProfile= repository.save(userProfile);
        return ResponseEntity.ok(updatedUserProfile);
    }

}

