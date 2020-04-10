package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class UserPrivateProfileResourceAssembler implements ResourceAssembler<UserPrivateProfile, Resource<UserPrivateProfile>> {

  @Override
  public Resource<UserPrivateProfile> toResource(UserPrivateProfile userPrivateProfile) {

    return new Resource<>(userPrivateProfile,
      linkTo(methodOn(UserProfileController.class).one(userPrivateProfile.getId())).withSelfRel());
  }

  public Resource<UserPrivateProfile> toResource(UserPrivateProfile userPrivateProfile, User user) {
    return new Resource<>(userPrivateProfile,
      linkTo(methodOn(UserPrivateProfileController.class).one(userPrivateProfile.getId())).withSelfRel(),
      linkTo(methodOn(UserController.class).getPrivateProfile(user.getId())).withSelfRel());
  }

}