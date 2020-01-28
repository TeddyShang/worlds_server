package  worlds.server;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserProfileController{
    private final UserProfileRepository repository;

    UserProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/userprofiles")
    List<UserProfile> all() {
        return repository.findAll();
    }

    @GetMapping("/userprofiles/{id}")
    UserProfile one(@PathVariable String id) {
        return repository.findById(id)
        .orElseThrow(() -> new UserProfileNotFoundException(id));
    }

}