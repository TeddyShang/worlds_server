package worlds.server;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    /**
     * @param email An email to find a user with
     * @return User user associated with that email
     */
    User findByEmail(String email);

}