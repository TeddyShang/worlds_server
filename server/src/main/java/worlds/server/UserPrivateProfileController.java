package worlds.server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;

@RestController
class UserPrivateProfileController{
    private final UserPrivateProfileRepository repository;
    private final UserPrivateProfileResourceAssembler assembler;

    UserPrivateProfileController(UserPrivateProfileRepository repository, UserPrivateProfileResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * Gets a single users private profile
     * @param id of the private profile
     * @return
     */
    @GetMapping(value = "/userprivateprofiles/{id}", produces = "application/json; charset=UTF-8")
    Resource<UserPrivateProfile> one(@PathVariable String id) {
        UserPrivateProfile userPrivateProfile = repository.findById(id)
        .orElseThrow(() -> new UserPrivateProfileNotFoundException(id));
        return assembler.toResource(userPrivateProfile);
    }

    /**
     * Allows edits to private profiles
     * @param userPrivateProfileInfo incoming private profile object
     * @param id of the private profile to edit
     * @return
     * @throws UserPrivateProfileNotFoundException
     */
    @PutMapping("/userprivateprofiles/{id}")
    ResponseEntity<UserPrivateProfile> updateUserPrivateProfile(@Valid @RequestBody UserProfile userPrivateProfileInfo,
    @PathVariable final String id) throws UserPrivateProfileNotFoundException {
        UserPrivateProfile userPrivateProfile = repository.findById(id)
        .orElseThrow(() -> new UserPrivateProfileNotFoundException(id));

        userPrivateProfile.setAddress(userPrivateProfile.getAddress());
        userPrivateProfile.setCreditCardNumber(userPrivateProfile.getCreditCardNumber());
        userPrivateProfile.setCreditExpirationDate(userPrivateProfile.getCreditCardNumber());
        userPrivateProfile.setCreditSecurityCode(userPrivateProfile.getCreditSecurityCode());
        userPrivateProfile.setPhoneNumber(userPrivateProfile.getPhoneNumber());

        final UserPrivateProfile updatedUserPrivateProfile= repository.save(userPrivateProfile);
        return ResponseEntity.ok(updatedUserPrivateProfile);
    }

}

