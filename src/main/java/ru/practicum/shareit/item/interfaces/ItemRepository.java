package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(ItemDto itemDto, Integer userId);

    Optional<Item> getById(Integer id);

    Item update(Item item);

    List<Item> getByOwner(Integer userId);

    List<Item> search(String text);
}
