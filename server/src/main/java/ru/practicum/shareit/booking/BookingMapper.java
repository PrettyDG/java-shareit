package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.extra.BookingStatusEnum;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBookingWithBookerAndItem(BookingDtoRequest bookingDtoRequest, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatusEnum.WAITING);
        return booking;
    }

    public static List<BookingDto> toBookingDtoList(Collection<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return List.of();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
