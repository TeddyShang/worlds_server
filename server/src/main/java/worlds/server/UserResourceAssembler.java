package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class UserResourceAssembler implements ResourceAssembler<User, Resource<User>> {

  @Override
  public Resource<User> toResource(User user) {

    return new Resource<>(user,
      linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
      linkTo(methodOn(UserController.class).all()).withRel("users"));
  }
}