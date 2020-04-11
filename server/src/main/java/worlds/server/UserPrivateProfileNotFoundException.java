package worlds.server;

@SuppressWarnings("serial")
class UserPrivateProfileNotFoundException extends RuntimeException {

    UserPrivateProfileNotFoundException(String id) {
        super("Could not find private profile with id: " + id);
    }
}