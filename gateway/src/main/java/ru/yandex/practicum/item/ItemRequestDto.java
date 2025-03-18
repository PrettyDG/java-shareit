package ru.yandex.practicum.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}