package worlds.server;

@SuppressWarnings("serial")
class UserNotFoundException extends RuntimeException {

    UserNotFoundException(String id) {
        super("Could not find employee with id: " + id);
    }
}