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
class BookingController{
    private final BookingRepository repository;
    private final BookingResourceAssembler assembler;

    BookingController(BookingRepository repository, BookingResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping(value = "/bookings", produces = "application/json; charset=UTF-8")
    Resources<Resource<Booking>> all() {
        List<Resource<Booking>> bookings = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());
        
        return new Resources<>(bookings,
            linkTo(methodOn(BookingController.class).all()).withSelfRel()); 
    }

    @GetMapping(value = "/bookings/{id}", produces = "application/json; charset=UTF-8")
    Resource<Booking> one(@PathVariable String id) {
        Booking booking = repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
        return assembler.toResource(booking);
    }
    
    /**
     * TODO: GET /bookings/{id}/mediametadata IVRE-179
     * When this call gets executed, we should return a list of mediametadata
     * Follow the format of the GET calls above, and make sure to dependency inject an instance of the mediametadata repository
     * You should make this list HATEOAS complient, so you will need to modify MediaMetaDataResourceAssembler.java and overload the toResource method
     * See UserProfileResourceAssembler.java for help and the method getProfile in UserController.java as implementation will be very similar
     * To test: You will need to modify LoadDatabase.java. Create several mediametadata objects and save several mediametadata objects, and store their IDs into a list
     * Then set a bookings's mediaIds attribute to that list
     * Navigating to the URL should return all of those mediametadata objects as well as a self-linked URL reference
     */

     /**
      * TODO: POST /bookings IVRE-182
      * When this call gets executed, we should validate the request body and save an
      * instance of a booking object into the repository
      */

}