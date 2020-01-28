package worlds.server;

@SuppressWarnings("serial")
class UserProfileNotFoundException extends RuntimeException {

    UserProfileNotFoundException(String id) {
        super("Could not find profile with id: " + id);
    }
}