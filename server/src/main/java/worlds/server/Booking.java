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
    private String[][] mediaIdsByRoom;
    private String[] tags;
    private String[][] rooms;
    private BookingPrivacy bookingPrivacy;
    private BookingStatus bookingStatus;
    private Boolean deletedBooking;

    Booking() {}

    /**
     * TODO: Change dateRequested to also be a long value to capture specific datetime
     * Constructor of what we expect from an incoming POST request
     * @param realtorId user id of the requesting realtor. Note: not their realtor id which is their state/accredited id
     * @param address 
     * @param dateRequested
     * @param locationCoordinates format "(long, lat, elevation)"
     * @param rooms [] containing [roomName, numPhotos, videoTrue/False]
     * @param tags
     */
    Booking(String realtorId, String address, String dateRequested, String locationCoordinates, String[][] rooms, String[] tags) {
        this.realtorId = realtorId;
        this.locationCoordinates = locationCoordinates;
        this.address = address;
        this.rooms = rooms;
        this.dateRequested = dateRequested;
        this.bookingPrivacy = BookingPrivacy.OPEN;
        this.bookingStatus = BookingStatus.PENDING;
        this.creatorId = null;
        this.dateCompleted = null;
        Date date = new Date();
        this.dateCreated = date.getTime();
        this.mediaIds = new String[0];
        this.mediaIdsByRoom = new String[rooms.length][0];
        this.tags = tags;
        this.deletedBooking = false;
    }

}