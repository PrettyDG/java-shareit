package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.extra.BookingStatusEnum;
import ru.practicum.shareit.extra.State;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(int userId, BookingDtoRequest bookingDtoRequest) {
        int itemId = bookingDtoRequest.getItemId();

        User booker = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не найден Item с данным id"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item not Available");
        }

        if (booker.getId().equals(item.getUser().getId())) {
            throw new ValidationException("Владелец не может забронировать свой же Item");
        }

        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new ValidationException("Дата начала бронирования равна дате окончания");
        }

        if (bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd())) {
            throw new ValidationException("Дата окончания бронирования раньше даты начала бронирования");
        }

        Booking booking = BookingMapper.toBookingWithBookerAndItem(bookingDtoRequest, booker, item);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approvedBooking(int userId, int bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id -" + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Не найден предмет с id - " + booking.getItem().getId()));

        if (item.getUser().getId() != userId) {
            throw new ValidationException("У " + item.getUser().getName() + " не найден предмет - " + item.getName());
        }

        if (booking.getStatus().equals(BookingStatusEnum.APPROVED)) {
            throw new ValidationException("Предмет уже находится в статусе APPROVED");
        }

        booking.setStatus(approved ? BookingStatusEnum.APPROVED : BookingStatusEnum.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id -" + bookingId + " не найдено"));

        User owner = booking.getItem().getUser();
        User booker = booking.getBooker();

        if (owner.getId() != userId && booker.getId() != userId) {
            throw new NotFoundException("У '" + owner.getName() + "' и '" + booker.getName() +
                    "' не найден предмет: '" + booking.getItem().getName() + "'!");
        }

        return booking;
    }

    @Override
    public Collection<Booking> getBookingsByBooker(int userId, State state) {
        List<Booking> bookings;

        switch (state) {
            case ALL -> {
                bookings = bookingRepository.findAllBookingsByBookerIdOrderByStartDesc(userId);
            }
            case CURRENT -> {
                bookings = bookingRepository.findAllCurrentBookingsByBookerId(userId, LocalDateTime.now());
            }
            case PAST -> {
                bookings = bookingRepository.findAllBookingsByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case FUTURE -> {
                bookings = bookingRepository.findAllBookingsByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case WAITING -> {
                bookings = bookingRepository.findAllBookingsByBookerIdAndStatusOrderByStartDesc(userId, BookingStatusEnum.WAITING);
            }
            case REJECTED -> {
                bookings = bookingRepository.findAllBookingsByBookerIdAndStatusOrderByStartDesc(userId, BookingStatusEnum.REJECTED);
            }
            default -> {
                throw new NotFoundException("Не найден State - " + state);
            }
        }

        return bookings;
    }

    @Override
    public Collection<Booking> getBookingsByOwner(int userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id - " + userId));
        Set<Integer> itemIds = itemRepository.findAllItemsByUserOrderByIdAsc(user).stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        List<Booking> bookings;

        switch (state) {
            case ALL -> {
                bookings = bookingRepository.findAllBookingsByItemIdInOrderByStartDesc(itemIds);
            }
            case CURRENT -> {
                bookings = bookingRepository.findAllCurrentBookingsByOwnerId(userId, LocalDateTime.now());
            }
            case PAST -> {
                bookings = bookingRepository.findAllBookingsByItemUserIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case FUTURE -> {
                bookings = bookingRepository.findAllBookingsByItemUserIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case WAITING -> {
                bookings = bookingRepository.findAllBookingsByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatusEnum.WAITING);
            }
            case REJECTED -> {
                bookings = bookingRepository.findAllBookingsByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatusEnum.REJECTED);
            }
            default -> {
                throw new NotFoundException("Не найден State - " + state);
            }
        }

        return bookings;
    }
}
