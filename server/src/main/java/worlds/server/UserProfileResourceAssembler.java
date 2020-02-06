package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class UserProfileResourceAssembler implements ResourceAssembler<UserProfile, Resource<UserProfile>> {

  @Override
  public Resource<UserProfile> toResource(UserProfile userProfile) {

    return new Resource<>(userProfile,
      linkTo(methodOn(UserProfileController.class).one(userProfile.getId())).withSelfRel(),
      linkTo(methodOn(UserProfileController.class).all()).withRel("userprofiles"));
  }

  public Resource<UserProfile> toResource(UserProfile userProfile, User user) {
    return new Resource<>(userProfile,
      linkTo(methodOn(UserProfileController.class).one(userProfile.getId())).withSelfRel(),
      linkTo(methodOn(UserController.class).getProfile(user.getId())).withSelfRel());
  }

}