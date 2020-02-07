package worlds.server;

import lombok.Data;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@Data
@EntityScan
public class BookingRoomInfo {

    public String name;
    public int photosAmount;
    public Boolean video;
    
    BookingRoomInfo(){}

    BookingRoomInfo(String roomName, int photoNum, Boolean video) {
        this.name = roomName;
        this.photosAmount = photoNum;
        this.video = video;
    }
}

