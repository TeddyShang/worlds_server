package worlds.server;

import lombok.Data;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Data
@Document(collection = "bookings")
public class Booking {
    @Id private String id;

    private Long dateCreated;
    private String dateRequested;
    private Long dateCompleted;
    private String realtorId;
    private String creatorId;
    private String locationCoordinates;
    private String address;
    private String[] mediaIds;
    private String[] tags;
    private String[][] rooms;
    private BookingPrivacy bookingPrivacy;
    private BookingStatus bookingStatus;

    Booking() {}

//switch rooms to BookingRoomInfo type later
//Issues with "Could not write JSON: Couldn't find PersistentEntity for type class [Lworlds.server.BookingRoomInfo;!;
// nested exception is com.fasterxml.jackson.databind.JsonMappingException: Couldn't find PersistentEntity for type 
//class [Lworlds.server.BookingRoomInfo;! (through reference chain: org.springframework.hateoas.Resource[\"content\"]->worlds.server.Booking[\"rooms\"])",
    Booking(String realtorId, String address, String dateRequested, String locationCoordinates, String[][] rooms, String[] tags) {
        this.realtorId = realtorId; // Currently being passed in their name
        this.locationCoordinates = locationCoordinates;
        this.address = address;
        this.rooms = rooms;
        this.dateRequested = dateRequested;
        this.bookingPrivacy = BookingPrivacy.OPEN;
        this.bookingStatus = BookingStatus.PENDING;
        Date date = new Date();
        this.dateCreated = date.getTime();
        this.mediaIds = new String[0];
        this.tags = tags;
    }

}