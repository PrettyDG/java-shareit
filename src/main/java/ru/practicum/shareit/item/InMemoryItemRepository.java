package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.interfaces.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemRepository implements ItemRepository {
    Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(ItemDto itemDto, Integer userId) {
        log.info("Создаётся item - {}, для пользователя - {}", itemDto, userId);

        Item item = new Item(getNextId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null);


        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item update(Item item) {
        log.info("Получен запрос на обновление Item - " + item);

        items.remove(item.getId());
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> getByOwner(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(Integer id) {
        log.info("Получен запрос на выдачу Item с id - " + id);

        return Optional.of(items.get(id));
    }

    private int getNextId() {
        int currentMaxId = items.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
