package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDtoResponse> comments;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private User owner;
}
