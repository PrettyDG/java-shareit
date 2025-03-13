package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.extra.State;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, bookingDtoRequest));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable int bookingId,
                                      @RequestParam boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approvedBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable int bookingId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return BookingMapper.toBookingDtoList(bookingService.getBookingsByBooker(userId, state));
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(defaultValue = "ALL") State state) {
        return BookingMapper.toBookingDtoList(bookingService.getBookingsByOwner(userId, state));
    }
}
