package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.extra.BookingStatusEnum;
import ru.practicum.shareit.extra.State;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mockMvc;
    private BookingDtoRequest bookingDtoRequest;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .user(user)
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .start(LocalDateTime.of(2025, 5, 10, 12, 0))
                .end(LocalDateTime.of(2025, 5, 11, 12, 0))
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.of(2025, 5, 10, 12, 0))
                .end(LocalDateTime.of(2025, 5, 11, 12, 0))
                .item(item)
                .booker(user)
                .status(BookingStatusEnum.WAITING)
                .build();
    }

    @Test
    void approvedBooking_shouldReturn200() throws Exception {
        when(bookingService.approvedBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));
    }

    @Test
    void getBookingById_shouldReturn200() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));
    }

    @Test
    void getBookingsByBooker_shouldReturn200() throws Exception {
        when(bookingService.getBookingsByBooker(anyInt(), any(State.class)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().name())));
    }

    @Test
    void getBookingsByOwner_shouldReturn200() throws Exception {
        when(bookingService.getBookingsByOwner(anyInt(), any(State.class)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().name())));
    }
}