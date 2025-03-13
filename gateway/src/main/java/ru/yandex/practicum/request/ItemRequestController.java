package ru.yandex.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @Valid @RequestBody ItemRequestDto request) {
        log.info("Добавление запроса: {}", request);
        return itemRequestClient.createRequest(userId, request);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @PathVariable int requestId) {
        log.info("Получение запроса по id: {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех запросов пользователя с id: {}", userId);
        return itemRequestClient.getAllRequestByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение всех запросов");
        return itemRequestClient.getAllRequest();
    }
}