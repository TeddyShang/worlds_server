package worlds.server;

import lombok.Data;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.data.annotation.Id;

@Data
@Document(collection = "users")
public class User{
    @Id public String id;

    public String firstName;
    public String lastName;
    public UserStatus userStatus;
    public UserType userType;
    public UserState userState;
    public String profileId;
    public String[] bookingIds;
    public Long dateCreated;
    public Long lastLoggedIn;
    public String realtorId;
    public String email;
    public String approvalText;
    private String hashedPassword;
    private Integer failedLogInAttempts;
    private Boolean deletedUser;

    User(){}
    
    User(String firstName, String lastName, UserType userType, String password, String email, String realtorId, String approvalText) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.userStatus = UserStatus.PENDING;
        this.userState = UserState.UNLOCKED;
        Date date = new Date();
        this.dateCreated = date.getTime();
        this.profileId = "";
        this.bookingIds = new String[0];
        this.lastLoggedIn = date.getTime();
        this.email = email;
        this.realtorId = realtorId;
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        this.hashedPassword = hashedPassword;
        this.failedLogInAttempts = 0;
        this.deletedUser = false;
        this.approvalText = approvalText;
    }
}