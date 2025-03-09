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
    public ItemDto getById(@PathVariable(name = "id") @Positive Integer id) {
        return itemService.get(id);
    }

    @GetMapping
    public Collection<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam(name = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestBody @Valid final ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable(name = "id") @Positive final Integer itemId,
                          @RequestBody final ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.update(itemId, itemDto, userId);
    }
}
