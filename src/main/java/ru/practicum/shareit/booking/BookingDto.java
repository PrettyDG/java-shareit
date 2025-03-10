package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.extra.BookingStatusEnum;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class BookingDto {
    private Integer id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatusEnum status;
}
