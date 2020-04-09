package worlds.server;

@SuppressWarnings("serial")
class UserNotFoundException extends RuntimeException {

    /**
     * 
     * @param id User Id
     */
    UserNotFoundException(String id) {
        super("Could not find employee with id: " + id);
    }
}