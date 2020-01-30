package  worlds.server;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import java.util.stream.Collectors;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
class MediaMetaDataController{
    private final MediaMetaDataRepository repository;
    private final MediaMetaDataResourceAssembler assembler;

    MediaMetaDataController(MediaMetaDataRepository repository, MediaMetaDataResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping(value = "/mediametadatas", produces = "application/json; charset=UTF-8")
    Resources<Resource<MediaMetaData>> all() {
        List<Resource<MediaMetaData>> mediaMetaDatas = repository.findAll().stream()
            .map(assembler::toResource)
            .collect(Collectors.toList());
        
        return new Resources<>(mediaMetaDatas,
            linkTo(methodOn(MediaMetaDataController.class).all()).withSelfRel()); 
    }

    @GetMapping(value = "/mediametadatas/{id}", produces = "application/json; charset=UTF-8")
    Resource<MediaMetaData> one(@PathVariable String id) {
        MediaMetaData mediaMetaData = repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
        return assembler.toResource(mediaMetaData);
    }

    /**
     * TODO: POST /mediametadatas IVRE-183
     * When this call gets executed, we should verify the body of the request then
     * save the object into the appropriate repository
     */

}