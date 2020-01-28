package worlds.server;

@SuppressWarnings("serial")
class MediaMetaDataNotFoundException extends RuntimeException {

    MediaMetaDataNotFoundException(String id) {
        super("Could not find media metadata with id: " + id);
    }
}