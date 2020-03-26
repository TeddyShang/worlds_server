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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper mapper;

    @MockBean
    private UserController userControllerMock;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private UserResourceAssembler userResourceAssemblerMock;

    @MockBean
    private UserProfileRepository userProfileRepositoryMock;

    @MockBean
    private UserProfileResourceAssembler userProfileResourceAssemblerMock;

    @MockBean
    private BookingRepository bookingRepositoryMock;

    @MockBean
    private BookingResourceAssembler bookingResourceAssemblerMock;


    private UserProtected user1;

    private MediaMetaData media;

    @Before
    public void setup() {
      mockMvc =
          MockMvcBuilders.standaloneSetup(new UserController(userRepositoryMock, userResourceAssemblerMock,
          userProfileRepositoryMock, userProfileResourceAssemblerMock, bookingRepositoryMock, 
          bookingResourceAssemblerMock))
              .build();
        setUpObjects();
    }

    private void setUpObjects() {
            
        user1.convertFrom(new User("John", "Doe", UserType.REALTOR, 
                                    "doey123", "jdoe1@gmail.com", "abcd", "Random text"));

        user1.setId("1");

        media = new MediaMetaData("creatorId", "room information", "url to media");
        media.setId("200");

    }

    @Test
    public void getAllUsers() throws Exception {

        when(userResourceAssemblerMock.toResource(user1)).thenReturn(new Resource<>(user1,
        linkTo(methodOn(UserController.class).one(user1.getId())).withSelfRel(),
        linkTo(methodOn(UserController.class).all()).withRel("users")));

        when(userRepositoryMock.findAll())
            .thenReturn(Collections.singletonList(user1));

        final ResultActions result = mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("links[0].rel", is("self")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].firstName", is("John")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].lastName", is("Doe")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].realtorId", is("abcd")));
    }
    @Test
    public void getOneUserwithId() throws Exception {
        
        when(userResourceAssemblerMock.toResource(user1)).thenReturn(new Resource<>(user1,
        linkTo(methodOn(UserController.class).one(user1.getId())).withSelfRel(),
        linkTo(methodOn(UserController.class).all()).withRel("users")));

        given(userRepositoryMock.findById("1")).willReturn(Optional.of(user1));

        final ResultActions result = mockMvc.perform(get("/users/" + "1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", is("John")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", is("Doe")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.realtorId", is("abcd")));

    }

    @Test
    public void postAddNewUser() throws Exception {

        given(userRepositoryMock.save(any(User.class))).willReturn(user1);
        given(userRepositoryMock.findById("1")).willReturn(Optional.of(user1));

        when(userResourceAssemblerMock.toResource(user1)).thenReturn(new Resource<>(user1,
        linkTo(methodOn(UserController.class).one(user1.getId())).withSelfRel(),
        linkTo(methodOn(UserController.class).all()).withRel("users")));
        
        final ResultActions result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsBytes(user1))
        .accept(MediaType.APPLICATION_JSON_UTF8)).andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", is("John")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", is("Doe")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.realtorId", is("abcd")));
    }
}