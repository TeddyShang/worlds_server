package worlds.server;

import lombok.Data;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Data
@Document(collection = "users")
class User{
    @Id private String id;

    private String firstName;
    private String lastName;
    private UserStatus userStatus;
    private UserType userType;
    private UserState userState;
    private String profileId;
    private String[] bookingIds;
    private Long dateCreated;
    private Long lastLoggedIn;

    User(){}
    
    User(String firstName, String lastName, UserType userType) {
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


    }
}