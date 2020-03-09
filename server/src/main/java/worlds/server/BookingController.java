package worlds.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;

import javax.validation.Valid;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class BookingController {
    static final String USER = "user";
	private final BookingRepository bookingRepository;
    private final BookingResourceAssembler bookingResourceAssembler;
    private final MediaMetaDataRepository mediaMetaDataRepository;
    private final MediaMetaDataResourceAssembler mediaMetaDataResourceAssembler;
    private final UserRepository userRepository;

    BookingController(BookingRepository bookingRepository, BookingResourceAssembler bookingResourceAssembler,
            MediaMetaDataRepository mediaMetaDataRepository,
            MediaMetaDataResourceAssembler mediaMetaDataResourceAssembler, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingResourceAssembler = bookingResourceAssembler;
        this.mediaMetaDataRepository = mediaMetaDataRepository;
        this.mediaMetaDataResourceAssembler = mediaMetaDataResourceAssembler;
        this.userRepository = userRepository;
    }

    /**
     * GET method
     * 
     * @return all bookings in the DB
     * Only map the bookings where its deletedBooking flag is set to false.
     */
    @GetMapping(value = "/bookings", produces = "application/json; charset=UTF-8")
    Resources<Resource<Booking>> all() {
        List<Resource<Booking>> bookings = bookingRepository.findAll().stream()
            .filter(booking -> booking.getDeletedBooking() == false)
            .map(bookingResourceAssembler::toResource).collect(Collectors.toList());
            return new Resources<>(bookings, linkTo(methodOn(BookingController.class).all()).withSelfRel());
    }

    /**
     * GET method
     * 
     * @param id of a booking
     * @return a specific booking or BookingNotFoundException if the id does not
     *         exist
     */
    @GetMapping(value = "/bookings/{id}", produces = "application/json; charset=UTF-8")
    Resource<Booking> one(@PathVariable String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        if (booking.getDeletedBooking() == false) {
            return bookingResourceAssembler.toResource(booking);
        } else {
            // Will return null because the feteched user is currently deleted.
            return null;
        }
    }

    /**
     * Get method
     * 
     * @param id of a specific booking
     * @return all mediametadatas belonging to a specific booking or
     *         BookingNotFoundException if the booking does not exist
     */
    @GetMapping(value = "/bookings/{id}/mediametadatas", produces = "application/json; charset=UTF-8")
    Resources<Resource<MediaMetaData>> getMediaMetaDatas(@PathVariable String id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getDeletedBooking() == false) {
        List<MediaMetaData> mediaMetaDataResults = new ArrayList<MediaMetaData>();
        String[] mediaMetaDataIds = booking.getMediaIds();

        // if the booking has no mediametadatas or is empty, we should return an empty
        // list
        if (mediaMetaDataIds == null || mediaMetaDataIds.length == 0) {
            List<Resource<MediaMetaData>> finalMediaMetaDatas = mediaMetaDataResults.stream()
                    .map(mediaMetaDataResourceAssembler::toResource).collect(Collectors.toList());

            return new Resources<>(finalMediaMetaDatas,
                    linkTo(methodOn(BookingController.class).getMediaMetaDatas(booking.getId())).withSelfRel());
        }

        // otherwise find all the mediametadatas and add them to the results list
        for (String mediaMetaDataId : mediaMetaDataIds) {
            MediaMetaData mediaMetaData = mediaMetaDataRepository.findById(mediaMetaDataId)
                    .orElseThrow(() -> new MediaMetaDataNotFoundException(mediaMetaDataId));
            mediaMetaDataResults.add(mediaMetaData);
        }

        List<Resource<MediaMetaData>> finalMediaMetaDatas = mediaMetaDataResults.stream()
                .map(mediaMetaDataResourceAssembler::toResource).collect(Collectors.toList());

        return new Resources<>(finalMediaMetaDatas,
                linkTo(methodOn(BookingController.class).getMediaMetaDatas(booking.getId())).withSelfRel());
        } else {

            //Return no multimedia if the specific booking is deleted.
            return null;
        }
    }

    /**
     * POST method
     * 
     * @param newBooking booking to be created
     * @return URI to new booking object
     * @throws URISyntaxException
     */
    @PostMapping("/bookings")
    ResponseEntity<?> newBooking(@RequestBody Booking newBooking) throws URISyntaxException {
        String realtorId = newBooking.getRealtorId();
        String address = newBooking.getAddress();
        String dateRequested = newBooking.getDateRequested();
        String[][] rooms = newBooking.getRooms();
        String[] tags = newBooking.getTags();
        String locationCoordinates = newBooking.getLocationCoordinates();
        Boolean deletedBooking = newBooking.getDeletedBooking();

        //first check if any of the arguments are null/empty
        if (realtorId == null || address == null || dateRequested == null || locationCoordinates == null
                || rooms == null || rooms.length == 0 || tags == null || deletedBooking == null) {
            return ResponseEntity.badRequest().body("Bad Request: Null Arguments");

        }

        //create the booking object and store it
        Booking booking = new Booking(realtorId, address, dateRequested, locationCoordinates, rooms, tags, deletedBooking);
        Booking savedBooking = bookingRepository.save(booking);
        
        //get the realtor and insert this booking into their list of ids
        //if the realtor does not exist then the request is not valid
        User realtor = userRepository.findById(realtorId)
        .orElseThrow(() -> new UserNotFoundException(realtorId));

        //add the new bookingId to the realtor and update the realtor's information
        String[] bookingIds = realtor.getBookingIds();
        List<String> tempList = new ArrayList<String>(Arrays.asList(bookingIds));
        tempList.add(savedBooking.getId());
        realtor.setBookingIds((tempList.toArray(new String[0])));
        userRepository.save(realtor);
        
        Resource<Booking> resource = bookingResourceAssembler.toResource(savedBooking);

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/bookings/{id}")
    ResponseEntity<Booking> updateBooking(@Valid @RequestBody Booking bookingInfo,
    @PathVariable final String id) throws BookingNotFoundException {
        Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new BookingNotFoundException("Booking does not exist with id ::" + id));

        BeanUtils.copyProperties(bookingInfo,booking);

        final Booking updatedBooking= bookingRepository.save(booking);
        return ResponseEntity.ok(updatedBooking);
    }
    /*
    *
    * Since we are deleting a booking, need to delete BOOKING and
    * Go through the USER associated with the bookings (can be content creator or realtor)
    * and mark these bookings as DELETED from their USER BOOKING ID ARRAYS.
    */
    @DeleteMapping("bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {

        //find booking ID 
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));

        //find user ID from the booking's realtor id part.
        User realtorBooking = userRepository.findById(booking.getRealtorId()).orElseThrow(() 
        -> new UserNotFoundException(booking.getRealtorId()));

        //find user ID from the booking's creator id part.
        User creatorBooking = userRepository.findById(booking.getCreatorId()).orElseThrow(() 
        -> new UserNotFoundException(booking.getCreatorId()));
        
        // get all bookings for that user.
        String [] allUserBookings = realtorBooking.getBookingIds();
        String [] allcreatorBookings = creatorBooking.getBookingIds();

        //store bookings as list.
        List<String> list = new ArrayList<String>(Arrays.asList(allUserBookings));
        List<String> list2 = new ArrayList<String>(Arrays.asList(allcreatorBookings));

        //remove the deleted booking from the list.
        list.remove(booking.getId());
        list2.remove(booking.getId());

        //save new list as an array.
        allUserBookings = list.toArray(new String[0]);
        allcreatorBookings = list2.toArray(new String[0]);

        //set booking id array to modified string[]
        realtorBooking.setBookingIds(allUserBookings);
        creatorBooking.setBookingIds(allcreatorBookings);
        
        //save new user instance in repo
        userRepository.save(realtorBooking);
        userRepository.save(creatorBooking);

        booking.setDeletedBooking(true);
        bookingRepository.save(booking);
        return ResponseEntity.noContent().build();
    }

}