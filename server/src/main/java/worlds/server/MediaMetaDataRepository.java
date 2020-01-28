package worlds.server;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaMetaDataRepository extends MongoRepository<MediaMetaData, String> {

}