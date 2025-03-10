package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable(name = "id") @Positive Integer id,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.get(id, userId);
    }

    @GetMapping
    public Collection<ItemDtoResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDtoResponse> search(@RequestParam(name = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDtoResponse create(@RequestBody @Valid final ItemDtoRequest itemDtoRequest,
                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.create(itemDtoRequest, userId);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse update(@PathVariable(name = "id") @Positive final Integer itemId,
                                  @RequestBody final ItemDtoRequest itemDtoRequest,
                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.update(itemId, itemDtoRequest, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable int itemId,
                                         @Valid @RequestBody CommentDtoRequest commentRequest) {
        return itemService.addComment(userId, itemId, commentRequest);
    }
}
