package worlds.server;

@SuppressWarnings("serial")
class BookingNotFoundException extends RuntimeException {

    BookingNotFoundException(String id) {
        super("Could not find booking with id: " + id);
    }
}