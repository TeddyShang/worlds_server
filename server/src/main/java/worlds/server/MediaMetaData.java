package worlds.server;

import lombok.Data;
import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document(collection = "mediametadatas")
class MediaMetaData {
    @Id private String id;

    private String creatorId;
    private Long dateUploaded;
    private String roomInformation;
    private String urlToMedia;

    MediaMetaData(){}

    MediaMetaData(String creatorId, String roomInformation, String urlToMedia) {
        this.creatorId = creatorId;
        this.roomInformation = roomInformation;
        this.urlToMedia = urlToMedia;
        Date date = new Date();
        this.dateUploaded = date.getTime();
    }
}