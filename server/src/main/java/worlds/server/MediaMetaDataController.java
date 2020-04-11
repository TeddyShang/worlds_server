package worlds.server;

import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;

import javax.validation.Valid;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class MediaMetaDataController{
    private final MediaMetaDataRepository repository;
    private final MediaMetaDataResourceAssembler assembler;

    MediaMetaDataController(MediaMetaDataRepository repository, MediaMetaDataResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * GET method
     * @return all mediametadatas in the database
     */
    @GetMapping(value = "/mediametadatas", produces = "application/json; charset=UTF-8")
    Resources<Resource<MediaMetaData>> all() {
        List<Resource<MediaMetaData>> mediaMetaDatas = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());
        
        return new Resources<>(mediaMetaDatas,
            linkTo(methodOn(MediaMetaDataController.class).all()).withSelfRel()); 
    }

    /**
     * GET method
     * 
     * @param id id of a mediametadata
     * @return A specific mediametadata
     * @throws MediaMetaDataNotFoundException if mediametadata id is not found
     */
    @GetMapping(value = "/mediametadatas/{id}", produces = "application/json; charset=UTF-8")
    Resource<MediaMetaData> one(@PathVariable String id) {
        MediaMetaData mediaMetaData = repository.findById(id)
        .orElseThrow(() -> new MediaMetaDataNotFoundException(id));
        return assembler.toResource(mediaMetaData);
    }

    /**
     * POST method
     * 
     * @param newMediaMetaData
     * @return Creates a new mediaMetaData in the database
     * @throws URISyntaxException if failed to create new mediametadata object
     */
    @PostMapping("/mediametadatas")
    ResponseEntity<?> newMediaMetaData(@RequestBody MediaMetaData newMediaMetaData) throws URISyntaxException {

        MediaMetaData mediametadata = new MediaMetaData(newMediaMetaData.getCreatorId(),
                newMediaMetaData.getRoomInformation(), newMediaMetaData.getUrlToMedia());

        Resource<MediaMetaData> resource = assembler.toResource(repository.save(mediametadata));
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * PUT method
     * 
     * @param updateMediaMetaData a mediametadata object with desired attributes
     * @param id  id of current mediametadata object
     * @return An updated version of current mediametadata object with new attributes
     * @throws MediaMetaDataNotFoundException 
     */
    @PutMapping("/mediametadatas/{id}")
    Resource<MediaMetaData> updateMediaMetadata(@Valid @RequestBody MediaMetaData updateMediaMetaData,
    @PathVariable final String id) throws MediaMetaDataNotFoundException {
        MediaMetaData current = repository.findById(id)
        .orElseThrow(() -> new MediaMetaDataNotFoundException(id));

        current.setCreatorId(updateMediaMetaData.getCreatorId());
        current.setRoomInformation(updateMediaMetaData.getRoomInformation());
        current.setUrlToMedia(updateMediaMetaData.getUrlToMedia());
        current.setDateUploaded(updateMediaMetaData.getDateUploaded());
        final MediaMetaData updatedMetaData = repository.save(current);
        return assembler.toResource(updatedMetaData);
    }

}