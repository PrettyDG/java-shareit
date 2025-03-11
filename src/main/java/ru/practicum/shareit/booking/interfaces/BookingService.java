package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoRequest;
import ru.practicum.shareit.extra.State;

import java.util.Collection;

public interface BookingService {

    Booking createBooking(int userId, BookingDtoRequest bookingDtoRequest);

    Booking approvedBooking(int userId, int bookingId, Boolean approved);

    Booking getBookingById(int userId, int bookingId);

    Collection<Booking> getBookingsByBooker(int userId, State state);

    Collection<Booking> getBookingsByOwner(int userId, State state);
}