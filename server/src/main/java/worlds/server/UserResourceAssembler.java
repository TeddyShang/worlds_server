package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class UserResourceAssembler implements ResourceAssembler<UserProtected, Resource<UserProtected>> {

  /**
   * @param userProtected 
   * @return userProtected
   * @return self link to userProtected 
   * @return link to all users
   */
  @Override
  public Resource<UserProtected> toResource(UserProtected userProtected) {

    return new Resource<>(userProtected,
      linkTo(methodOn(UserController.class).one(userProtected.getId())).withSelfRel(),
      linkTo(methodOn(UserController.class).all()).withRel("users"));
  }
}