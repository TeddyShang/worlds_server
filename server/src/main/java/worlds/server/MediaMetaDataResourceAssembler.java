package worlds.server;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class MediaMetaDataResourceAssembler implements ResourceAssembler<MediaMetaData, Resource<MediaMetaData>> {

  @Override
  public Resource<MediaMetaData> toResource(MediaMetaData mediaMetaData) {
    return new Resource<>(mediaMetaData,
      linkTo(methodOn(MediaMetaDataController.class).one(mediaMetaData.getId())).withSelfRel(),
      linkTo(methodOn(MediaMetaDataController.class).all()).withRel("mediametadatas"));
  }

  /*
  //Link to the getMediaMetaData method
  public Resource<List<MediaMetaData>> toResource(List<MediaMetaData> mediaMetaData, Booking booking) {
    return new Resource<>(mediaMetaData,
      linkTo(methodOn(MediaMetaDataController.class).one(booking.getId())).withSelfRel(),
      linkTo(methodOn(BookingController.class).getMediaMetaData(booking.getId())).withRel("mediametadataAll"));

  }
  */
  
}