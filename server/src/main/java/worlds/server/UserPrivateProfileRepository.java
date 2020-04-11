package worlds.server;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPrivateProfileRepository extends MongoRepository<UserPrivateProfile, String> {

}