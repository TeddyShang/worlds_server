package  worlds.server;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class BookingController{
    private final BookingRepository repository;
    private final BookingResourceAssembler assembler;
    private final MediaMetaDataRepository mediaMetaDataRepository;
    private final MediaMetaDataResourceAssembler mediaMetaDataResourceAssembler;

    BookingController(BookingRepository repository, BookingResourceAssembler assembler, MediaMetaDataRepository mediaMetaDataRepository, MediaMetaDataResourceAssembler mediaMetaDataResourceAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.mediaMetaDataRepository = mediaMetaDataRepository;
        this.mediaMetaDataResourceAssembler = mediaMetaDataResourceAssembler;
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

     /*
    @GetMapping(value = "/bookings/{id}/mediametadata", produces  = "application/json; charset=UTF-8")
    //Return a list of mediametadata
    Resource<List<MediaMetaData>> getMediaMetaData(@PathVariable String id) {
        
        //use id to find booking
        Booking booking = repository.findById(id)
        .orElseThrow(() -> new BookingNotFoundException(id));

        //get string[] of meta ids from that booking.
        String [] mediaMetaId = booking.getMediaIds();
        List<MediaMetaData> results = new ArrayList<MediaMetaData>();
        
        for (String mediaMetaIds : mediaMetaId) {
            //place these mediametadata stuff into result
            MediaMetaData result = mediaMetaDataRepository.findById(mediaMetaIds)
            .orElseThrow(() -> new MediaMetaDataNotFoundException(mediaMetaIds));
            
            //add these to the list
            results.add(result);
        }
        
        //return a list of these ids back along with the booking that it is associated with.
        return mediaMetaDataResourceAssembler.toResource(results, booking);
    }
    */
    
    @PostMapping("/bookings")
    ResponseEntity<?> newBooking(@RequestBody Booking newBooking) throws URISyntaxException {
        Booking booking = new Booking(newBooking.getRealtorId(), newBooking.getLocationCoordinates(),
                newBooking.getAddress(), newBooking.getDetails());

        Resource<Booking> resource = assembler.toResource(repository.save(booking));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

}