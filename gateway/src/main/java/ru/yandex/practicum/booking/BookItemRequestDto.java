package ru.yandex.practicum.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookItemRequestDto {
    private Integer itemId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    @FutureOrPresent
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    @Future
    private LocalDateTime end;
}