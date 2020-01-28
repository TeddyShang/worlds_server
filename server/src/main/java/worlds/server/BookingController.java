package  worlds.server;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BookingController{
    private final BookingRepository repository;

    BookingController(BookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/bookings")
    List<Booking> all() {
        return repository.findAll();
    }

    @GetMapping("/bookings/{id}")
    Booking one(@PathVariable String id) {
        return repository.findById(id)
        .orElseThrow(() -> new BookingNotFoundException(id));
    }

}