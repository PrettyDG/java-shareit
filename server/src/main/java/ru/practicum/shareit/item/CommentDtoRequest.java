package ru.practicum.shareit.item;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoRequest {

    private String text;
}