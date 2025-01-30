package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto update(Integer itemId, ItemDto itemDto, Integer userId);

    ItemDto get(Integer itemId);

    Collection<ItemDto> getByOwner(Integer userId);

    Collection<ItemDto> search(String text);
}
