package worlds.server;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "userprivateprofiles")
class UserPrivateProfile {
    @Id private String id;
    
    private String address;
    private String phoneNumber;
    private String creditCardNumber;
    private String creditExpirationDate;
    private String creditSecurityCode;

    UserPrivateProfile(){
        this.address = "";
        this.phoneNumber = "";
        this.creditCardNumber = "";
        this.creditExpirationDate = "";
        this.creditSecurityCode = "";
    }

}