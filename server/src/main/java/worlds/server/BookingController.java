package worlds.server;

import java.util.ArrayList;
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
    

    @GetMapping(value = "/bookings/{id}/mediametadatas", produces  = "application/json; charset=UTF-8")
    Resources<Resource<MediaMetaData>> getMediaMetaDatas(@PathVariable String id) {
        Booking booking = repository.findById(id)
        .orElseThrow(() -> new BookingNotFoundException(id));

        String[] mediaMetaDataIds = booking.getMediaIds();

        List<MediaMetaData> mediaMetaDataResults = new ArrayList<MediaMetaData>();

        for (String mediaMetaDataId :mediaMetaDataIds){
            MediaMetaData mediaMetaData = mediaMetaDataRepository.findById(mediaMetaDataId)
            .orElseThrow(() -> new MediaMetaDataNotFoundException(mediaMetaDataId));
            mediaMetaDataResults.add(mediaMetaData);
        }

        List<Resource<MediaMetaData>> finalMediaMetaDatas = mediaMetaDataResults.stream()
        .map(mediaMetaDataResourceAssembler::toResource)
        .collect(Collectors.toList());

        return new Resources<>(finalMediaMetaDatas,
        linkTo(methodOn(BookingController.class).getMediaMetaDatas(booking.getId())).withSelfRel()); 
    }
    
    @PostMapping("/bookings")
    ResponseEntity<?> newBooking(@RequestBody Booking newBooking) throws URISyntaxException {
        Booking booking = new Booking(newBooking.getRealtorId(), newBooking.getLocationCoordinates(),
                newBooking.getAddress(), newBooking.getRooms());

        Resource<Booking> resource = assembler.toResource(repository.save(booking));

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

}