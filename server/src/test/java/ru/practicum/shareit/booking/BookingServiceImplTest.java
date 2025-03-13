package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.extra.BookingStatusEnum;
import ru.practicum.shareit.extra.State;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Laptop")
                .description("A powerful laptop")
                .available(true)
                .user(user)
                .build();

        booking = Booking.builder()
                .id(1)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatusEnum.WAITING)
                .build();
    }

    @Test
    void createBooking_ShouldCreateBooking() {
        User owner = User.builder()
                .id(1)
                .name("Owner")
                .email("owner@example.com")
                .build();

        User booker = User.builder()
                .id(2)
                .name("Booker")
                .email("booker@example.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("Laptop")
                .description("A powerful laptop")
                .available(true)
                .user(owner)
                .build();

        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking expectedBooking = Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .status(BookingStatusEnum.WAITING)
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.createBooking(booker.getId(), bookingDtoRequest);

        assertNotNull(actualBooking);
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        assertEquals(expectedBooking.getBooker(), actualBooking.getBooker());
        assertEquals(expectedBooking.getItem(), actualBooking.getItem());
        assertEquals(expectedBooking.getStart(), actualBooking.getStart());
        assertEquals(expectedBooking.getEnd(), actualBooking.getEnd());
        assertEquals(expectedBooking.getStatus(), actualBooking.getStatus());

        verify(userRepository).findById(booker.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).save(any(Booking.class));
    }


    @Test
    void approvedBooking_ShouldApproveBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.approvedBooking(user.getId(), booking.getId(), true);

        assertEquals(BookingStatusEnum.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingsByBooker_ShouldReturnAllBookings() {
        when(bookingRepository.findAllBookingsByBookerIdOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.ALL).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingById_ShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), 999)
        );

        assertTrue(exception.getMessage().contains("не найдено"));
    }

    @Test
    void approvedBooking_ShouldRejectBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.approvedBooking(user.getId(), booking.getId(), false);

        assertEquals(BookingStatusEnum.REJECTED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approvedBooking_ShouldThrowExceptionWhenAlreadyApproved() {
        booking.setStatus(BookingStatusEnum.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(
                ValidationException.class,
                () -> bookingService.approvedBooking(user.getId(), booking.getId(), true)
        );
    }

    @Test
    void getBookingsByBooker_ShouldReturnCurrentBookings() {
        when(bookingRepository.findAllCurrentBookingsByBookerId(eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.CURRENT).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllCurrentBookingsByBookerId(eq(user.getId()), any(LocalDateTime.class));
    }


    @Test
    void getBookingsByBooker_ShouldReturnPastBookings() {
        when(bookingRepository.findAllBookingsByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now()))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.PAST).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now());
    }

    @Test
    void getBookingsByBooker_ShouldReturnFutureBookings() {
        when(bookingRepository.findAllBookingsByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now()))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.FUTURE).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now());
    }

    @Test
    void getBookingsByBooker_ShouldReturnWaitingBookings() {
        booking.setStatus(BookingStatusEnum.WAITING);
        when(bookingRepository.findAllBookingsByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.WAITING))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.WAITING).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.WAITING);
    }

    @Test
    void getBookingsByBooker_ShouldReturnRejectedBookings() {
        booking.setStatus(BookingStatusEnum.REJECTED);
        when(bookingRepository.findAllBookingsByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.REJECTED))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), State.REJECTED).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.REJECTED);
    }

    @Test
    void getBookingsByBooker_ShouldThrowIllegalArgumentExceptionForInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingsByBooker(1, State.valueOf("ds"));
        });
    }

    @Test
    void getBookingsByOwner_ShouldReturnAllBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllItemsByUserOrderByIdAsc(user)).thenReturn(List.of(item));
        when(bookingRepository.findAllBookingsByItemIdInOrderByStartDesc(Set.of(item.getId())))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.ALL).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).findAllItemsByUserOrderByIdAsc(user);
        verify(bookingRepository).findAllBookingsByItemIdInOrderByStartDesc(Set.of(item.getId()));
    }

    @Test
    void getBookingsByOwner_ShouldReturnCurrentBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllCurrentBookingsByOwnerId(eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.CURRENT).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllCurrentBookingsByOwnerId(eq(user.getId()), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwner_ShouldReturnPastBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingsByItemUserIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.PAST).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByItemUserIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwner_ShouldReturnFutureBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingsByItemUserIdAndStartAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.FUTURE).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByItemUserIdAndStartAfterOrderByStartDesc(eq(user.getId()), any(LocalDateTime.class));
    }

    @Test
    void getBookingsByOwner_ShouldReturnWaitingBookings() {
        booking.setStatus(BookingStatusEnum.WAITING);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingsByItemUserIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.WAITING))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.WAITING).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByItemUserIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.WAITING);
    }

    @Test
    void getBookingsByOwner_ShouldReturnRejectedBookings() {
        booking.setStatus(BookingStatusEnum.REJECTED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingsByItemUserIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.REJECTED))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByOwner(user.getId(), State.REJECTED).stream().toList();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());

        verify(bookingRepository).findAllBookingsByItemUserIdAndStatusOrderByStartDesc(user.getId(), BookingStatusEnum.REJECTED);
    }

    @Test
    void getBookingsByOwner_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsByOwner(999, State.ALL)
        );

        assertTrue(exception.getMessage().contains("Не найден пользователь с id"));
    }

    @Test
    void getBookingsByOwner_ShouldThrowIllegalArgumentException_ForInvalidState() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingsByOwner(user.getId(), State.valueOf("INVALID_STATE"))
        );

        assertTrue(exception.getMessage().contains("No enum constant"));
    }

}
