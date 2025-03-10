package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.extra.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    //Бронирования пользователей:
    List<Booking> findAllBookingsByBookerIdOrderByStartDesc(int bookerId);  // State - ALL

    @Query("""
            SELECT b from Booking AS b
            WHERE b.booker.id = :bookerId
            AND b.start <= :time
            AND b.end >= :time
            """)
    List<Booking> findAllCurrentBookingsByBookerId(int bookerId, LocalDateTime time); // State - CURRENT

    List<Booking> findAllBookingsByBookerIdAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime time); // State - PAST

    List<Booking> findAllBookingsByBookerIdAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime time); // State - FUTURE

    List<Booking> findAllBookingsByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingStatusEnum status); // State - WAITING, REJECTED

    //Бронирования владельца:
    List<Booking> findAllBookingsByItemIdInOrderByStartDesc(Set<Integer> itemIds); // State - ALL

    @Query("""
            SELECT b from Booking AS b
            WHERE b.item.user.id = :ownerId
            AND b.start <= :time
            AND b.end >= :time
            """)
    List<Booking> findAllCurrentBookingsByOwnerId(int ownerId, LocalDateTime time); // State - CURRENT

    List<Booking> findAllBookingsByItemUserIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime time); // State - PAST

    List<Booking> findAllBookingsByItemUserIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime time); // State - FUTURE

    List<Booking> findAllBookingsByItemUserIdAndStatusOrderByStartDesc(int ownerId, BookingStatusEnum status); // State - WAITING, REJECTED
}
