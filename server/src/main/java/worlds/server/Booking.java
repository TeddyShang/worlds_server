package worlds.server;

import lombok.Data;
import java.util.Date;

import org.springframework.data.annotation.Id;

@Data
class Booking {
    @Id private String id;

    private Long dateCreated;
    private Long dataCompleted;
    private String realtorId;
    private String creatorId;
    private String locationCoordinates;
    private String address;
    private String[] mediaIds;
    private String[] tags;
    private String[] details;
    private BookingPrivacy bookingPrivacy;
    private BookingStatus bookingStatus;

    Booking() {}

    Booking(String realtorId, String locationCoordinates, String address, String[] details) {
        this.realtorId = realtorId;
        this.locationCoordinates = locationCoordinates;
        this.address = address;
        this.details = details;
        this.bookingPrivacy = BookingPrivacy.OPEN;
        this.bookingStatus = BookingStatus.PENDING;
        Date date = new Date();
        this.dateCreated = date.getTime();
    }


}