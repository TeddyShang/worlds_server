package worlds.server;

import lombok.Data;

import org.springframework.data.annotation.Id;

@Data

class User {
    @Id private String id;

    private String firstName;
    private String lastName;

    User(){}
    
    User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}