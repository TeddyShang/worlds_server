package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class BookingResourceAssembler implements ResourceAssembler<Booking, Resource<Booking>> {

  @Override
  public Resource<Booking> toResource(Booking booking) {

    return new Resource<>(booking,
      linkTo(methodOn(BookingController.class).one(booking.getId())).withSelfRel(),
      linkTo(methodOn(BookingController.class).all()).withRel("bookings"));
  }
}