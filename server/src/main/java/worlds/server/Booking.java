package worlds.server;

import lombok.Data;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

class BookingRoomInfo {

    public String Name;
    public int PhotosAmount;
    public Boolean Video;

    BookingRoomInfo(String roomName, int photoNum, Boolean video) {
        this.Name = roomName;
        this.PhotosAmount = photoNum;
        this.Video = video;
    }
}


@Data
@Document(collection = "bookings")
public class Booking {
    @Id private String id;

    private Long dateCreated;
    private Long dateRequested;
    private Long dateCompleted;
    private String realtorId;
    private String creatorId;
    private String locationCoordinates;
    private String address;
    private String[] mediaIds;
    private String[] tags;
    private BookingRoomInfo[] rooms;
    private BookingPrivacy bookingPrivacy;
    private BookingStatus bookingStatus;

    Booking() {}

    Booking(String realtorId, String address, String dateRequested, BookingRoomInfo[] rooms) {
        this.realtorId = realtorId; // Currently being passed in their name
        // this.locationCoordinates = locationCoordinates;
        this.address = address;
        this.rooms = rooms;
        Date requested = new Date(dateRequested);
        this.dateRequested = requested.getTime();

        this.bookingPrivacy = BookingPrivacy.OPEN;
        this.bookingStatus = BookingStatus.PENDING;
        Date date = new Date();
        this.dateCreated = date.getTime();
    }


}