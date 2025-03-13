package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private int requestId;

    @NotBlank
    private String description;
}