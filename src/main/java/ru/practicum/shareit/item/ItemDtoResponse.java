package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoResponse {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}