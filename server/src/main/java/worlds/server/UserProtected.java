package worlds.server;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
class UserProtected extends User {
    
    UserProtected(){
    }

    /**
     * @param user 
     * 
     * Takes in a User user and sets a UserProtected "protectedUser" with the following fields.
     */
    public void convertFrom(User user) {
        this.id = user.id;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.userStatus = user.userStatus;
        this.userType = user.userType;
        this.userState = user.userState;
        this.profileId = user.profileId;
        this.bookingIds = user.bookingIds;
        this.dateCreated = user.dateCreated;
        this.lastLoggedIn = user.lastLoggedIn;
        this.realtorId = user.realtorId;
        this.email = user.email;
        this.privateProfileId = user.privateProfileId;
    }
}