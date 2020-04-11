package worlds.server;

@SuppressWarnings("serial")
class MediaMetaDataNotFoundException extends RuntimeException {
    
    /**
     * 
     * @param id mediametadata id
     */
    MediaMetaDataNotFoundException(String id) {
        super("Could not find media metadata with id: " + id);
    }
}