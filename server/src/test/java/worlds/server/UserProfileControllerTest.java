package worlds.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.core.Is.is;

import java.util.Collections;
import java.util.Optional;

//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@RunWith(SpringRunner.class)
@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileController userProfileControllerMock;

    //We are mocking the UserProfileController Constructor that takes these params.
    @MockBean
    private UserProfileRepository userProfileRepositoryMock;

    @MockBean
    private UserProfileResourceAssembler userProfileResourceAssemblerMock;

    private static final String ID = "1";
    private UserProfile person;
    private User user;

    @Before
    public void setup() {
      mockMvc =
          MockMvcBuilders.standaloneSetup(new UserProfileController(userProfileRepositoryMock, userProfileResourceAssemblerMock))
              .build();
        setupPerson();
    }

    private void setupPerson() {
        user = new User();
        user.setProfileId(ID);
        person = new UserProfile();
        person.setAboutMe("This is about me");
        person.setUrlToProfilePicture("facebook.com");
        person.setProfessionalExperience("2 years of experience.");
        person.setId(ID);
    }

    @Test
    public void getAllUserProfilesTest() throws Exception {

        when(userProfileResourceAssemblerMock.toResource(person)).thenReturn(new Resource<UserProfile>(person,
        linkTo(methodOn(UserProfileController.class).one(person.getId())).withSelfRel(),
        linkTo(methodOn(UserProfileController.class).all()).withRel("userprofiles")));

        when(userProfileRepositoryMock.findAll())
            .thenReturn(Collections.singletonList(person));

        final ResultActions result = mockMvc.perform(get("/userprofiles").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result 
        .andExpect(MockMvcResultMatchers.jsonPath("links[0].rel", is("self")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].aboutMe", is("This is about me")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].urlToProfilePicture", is("facebook.com")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].professionalExperience", is("2 years of experience.")));
    }

    @Test
    public void getOneProfilewithId() throws Exception {
        
        when(userProfileResourceAssemblerMock.toResource(person)).thenReturn(new Resource<>(person,
        linkTo(methodOn(UserProfileController.class).one(person.getId())).withSelfRel(),
        linkTo(methodOn(UserController.class).getProfile(user.getId())).withSelfRel()));

       given(userProfileRepositoryMock.findById(ID)).willReturn(Optional.of(person));
        final ResultActions result = mockMvc.perform(get("/userprofiles/" + "1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.aboutMe", is("This is about me")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.urlToProfilePicture", is("facebook.com")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.professionalExperience", is("2 years of experience.")));

    }

}