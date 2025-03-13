package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @Valid @RequestBody ItemRequestDto requestDto) {

        log.info("Добавление предмета: {}, пользователем с id: {}", requestDto, userId);
        return itemClient.createItem(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int itemId,
                                             @RequestBody ItemRequestDto requestDto) {
        log.info("Обновление предмета: {}, пользователем с id: {}", requestDto, userId);
        return itemClient.updateItem(userId, itemId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PathVariable int itemId) {
        log.info("Получение предмета по id: {}, пользователем с id: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех предметов пользователем с id: {}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam String text) {
        log.info("Поиск предметов по запросу: '{}', пользователем с id: {}", text, userId);

        if (text == null || text.trim().isEmpty()) {
            log.info("Пустой запрос, возвращаем пустую коллекцию");
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int itemId,
                                             @Valid @RequestBody CommentRequestDto requestDto) {
        log.info("Добавление комментария к предмету с id: {}", itemId);
        return itemClient.addComment(userId, itemId, requestDto);
    }
}