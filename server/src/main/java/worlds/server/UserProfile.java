package worlds.server;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
class UserProfile {
    @Id private String id;
    
    private String aboutMe;
    private String urlToProfilePicture;
    private String professionalExperience;
    UserProfile(){}

}