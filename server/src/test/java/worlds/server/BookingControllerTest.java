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
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper mapper;

    @MockBean
    private BookingController bookingControllerMock;

    //We are mocking the UserProfileController Constructor that takes these params.
    @MockBean
    private BookingRepository bookingRepositoryMock;

    @MockBean
    private BookingResourceAssembler bookingResourceAssemblerMock;

    @MockBean
    private MediaMetaDataRepository mediaMetaDataRepositoryMock;

    @MockBean
    private MediaMetaDataResourceAssembler mediaMetaDataResourceAssembler;

    @MockBean
    private UserRepository userRepositoryMock;

    private Booking booking1;

    private MediaMetaData media;
    private String[] mediaids;
    private User user;

    @Before
    public void setup() {
      mockMvc =
          MockMvcBuilders.standaloneSetup(new BookingController(bookingRepositoryMock, bookingResourceAssemblerMock,
          mediaMetaDataRepositoryMock, mediaMetaDataResourceAssembler, userRepositoryMock))
              .build();
        setUpObjects();
    }

    private void setUpObjects() {
        String[][] rooms = new String[][] {
            {"living room","4", "false"},
            {"bathroom","2", "false"}};

        mediaids = new String[] {"200"};

        String [] bookingid = new String[] {"300"};
        user = new User();
        user.setId("1");
        user.setBookingIds(bookingid);

        booking1 = new Booking("1", "Main Street", "02/02/20", "1.2.3.1", rooms);
        booking1.setId("1");
        booking1.setMediaIds(mediaids);

        media = new MediaMetaData("creatorId", "room information", "url to media");
        media.setId("200");

    }

    @Test
    public void getAllBookings() throws Exception {

        when(bookingResourceAssemblerMock.toResource(booking1)).thenReturn(new Resource<>(booking1,
        linkTo(methodOn(BookingController.class).one(booking1.getId())).withSelfRel(),
        linkTo(methodOn(BookingController.class).all()).withRel("bookings")));

        when(bookingRepositoryMock.findAll())
            .thenReturn(Collections.singletonList(booking1));

        final ResultActions result = mockMvc.perform(get("/bookings").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("links[0].rel", is("self")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].dateRequested", is("02/02/20")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].locationCoordinates", is("1.2.3.1")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].address", is("Main Street")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].realtorId", is("1")));
    }
    @Test
    public void getOneBookingwithId() throws Exception {
        
        when(bookingResourceAssemblerMock.toResource(booking1)).thenReturn(new Resource<>(booking1,
        linkTo(methodOn(BookingController.class).one(booking1.getId())).withSelfRel(),
        linkTo(methodOn(BookingController.class).all()).withRel("bookings")));

        given(bookingRepositoryMock.findById("1")).willReturn(Optional.of(booking1));

        final ResultActions result = mockMvc.perform(get("/bookings/" + "1").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.dateRequested", is("02/02/20")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.locationCoordinates", is("1.2.3.1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.address", is("Main Street")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.realtorId", is("1")));

    }

    @Test
    public void getAllMetadatasfortheOneBooking() throws Exception {
                
        when(bookingResourceAssemblerMock.toResource(booking1)).thenReturn(new Resource<>(booking1,
        linkTo(methodOn(BookingController.class).one(booking1.getId())).withSelfRel(),
        linkTo(methodOn(BookingController.class).all()).withRel("bookings")));

        given(bookingRepositoryMock.findById("1")).willReturn(Optional.of(booking1));

        when(mediaMetaDataResourceAssembler.toResource(media)).thenReturn(new Resource<>(media,
        linkTo(methodOn(MediaMetaDataController.class).one(media.getId())).withSelfRel(),
        linkTo(methodOn(MediaMetaDataController.class).all()).withRel("mediametadatas")));

        given(mediaMetaDataRepositoryMock.findById("200")).willReturn(Optional.of(media));

        final ResultActions result = mockMvc.perform(get("/bookings/1/mediametadatas").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("links[0].rel", is("self")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].id", is("200")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].roomInformation", is("room information")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].urlToMedia", is("url to media")))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].creatorId", is("creatorId")));

    }

    @Test
    public void postAddNewBooking() throws Exception {

        given(userRepositoryMock.save(any(User.class))).willReturn(user);
        given(bookingRepositoryMock.save(any(Booking.class))).willReturn(booking1);
        given(userRepositoryMock.findById("1")).willReturn(Optional.of(user));

        when(bookingResourceAssemblerMock.toResource(booking1)).thenReturn(new Resource<>(booking1,
        linkTo(methodOn(BookingController.class).one(booking1.getId())).withSelfRel(),
        linkTo(methodOn(BookingController.class).all()).withRel("bookings")));
        
        final ResultActions result = mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON_UTF8).content(mapper.writeValueAsBytes(booking1))
        .accept(MediaType.APPLICATION_JSON_UTF8)).andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated());
        result
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is("1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.dateRequested", is("02/02/20")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.locationCoordinates", is("1.2.3.1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.address", is("Main Street")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.realtorId", is("1")));
    }
}