package  worlds.server;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MediaMetaDataController{
    private final MediaMetaDataRepository repository;

    MediaMetaDataController(MediaMetaDataRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/mediametadatas")
    List<MediaMetaData> all() {
        return repository.findAll();
    }

    @GetMapping("/mediametadatas/{id}")
    MediaMetaData one(@PathVariable String id) {
        return repository.findById(id)
        .orElseThrow(() -> new MediaMetaDataNotFoundException(id));
    }

}