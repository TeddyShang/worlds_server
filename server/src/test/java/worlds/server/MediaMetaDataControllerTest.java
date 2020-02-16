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
@WebMvcTest(MediaMetaDataController.class)
public class MediaMetaDataControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaMetaDataController mDataControllerMock;

    //We are mocking the UserProfileController Constructor that takes these params.
    @MockBean
    private MediaMetaDataRepository mDataRepositoryMock;

    @MockBean
    private MediaMetaDataResourceAssembler mDataResourceAssemblerMock;

    private static final String ID = "1";
    private MediaMetaData metaData;

    private String creatorId = "jd12";
    private String roomInformation = "master";
    private String urlToMedia = "world.room1.co";

    @Before
    public void setup() {
        mockMvc =
          MockMvcBuilders.standaloneSetup(new MediaMetaDataController(
              mDataRepositoryMock, mDataResourceAssemblerMock))
              .build();
        setupMetaData();
    }

    private void setupMetaData() {
        metaData = new MediaMetaData(creatorId, roomInformation, urlToMedia);
    }

    @Test
    public void testGetAllUserProfiles() throws Exception {

        when(mDataResourceAssemblerMock.toResource(metaData)).thenReturn(new Resource<MediaMetaData>(metaData,
        linkTo(methodOn(MediaMetaDataController.class).one(metaData.getId())).withSelfRel(),
        linkTo(methodOn(MediaMetaDataController.class).all()).withRel("mediametadatas")));

        when(mDataRepositoryMock.findAll())
            .thenReturn(Collections.singletonList(metaData));

        final ResultActions result = mockMvc.perform(get("/mediametadatas").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result 
        .andExpect(MockMvcResultMatchers.jsonPath("links[0].rel", is("self")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].creatorId", is("jd12")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].roomInformation", is("master")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].urlToMedia", is("world.room1.co")));
    }

    @Test
    public void testOneMetaDatawithId() throws Exception {
        metaData.setId(ID);
        when(mDataResourceAssemblerMock.toResource(metaData)).thenReturn(new Resource<>(metaData,
        linkTo(methodOn(MediaMetaDataController.class).one(metaData.getId())).withSelfRel()));

       given(mDataRepositoryMock.findById(ID)).willReturn(Optional.of(metaData));
        final ResultActions result = mockMvc.perform(get("/mediametadatas/" + "1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.creatorId", is("jd12")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.roomInformation", is("master")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.urlToMedia", is("world.room1.co")));

    }

}