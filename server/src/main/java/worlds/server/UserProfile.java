package worlds.server;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "userprofiles")
class UserProfile {
    @Id private String id;
    
    private String aboutMe;
    private String urlToProfilePicture;
    private String professionalExperience;
    UserProfile(){}

}