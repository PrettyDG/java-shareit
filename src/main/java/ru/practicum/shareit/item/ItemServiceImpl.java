package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        if (userRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id - " + userId);
        }
        return ItemMapper.toItemDto(itemRepository.create(itemDto, userId));
    }

    @Override
    public ItemDto update(Integer itemId, ItemDto itemDto, Integer userId) {
        if (userRepository.getById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id - " + userId);
        } else if (itemRepository.getById(itemId).isEmpty()) {
            throw new NotFoundException("Не найден item с id - " + itemId);
        }

        Item oldItem = itemRepository.getById(itemId).get();

        if (!Objects.equals(oldItem.getOwnerId(), userId)) {
            throw new ValidationException("Item с id - " + itemId + " не принаджелит пользователю - " + userId);
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            itemDto.setDescription(oldItem.getDescription());
        }

        itemDto.setId(oldItem.getId());
        itemDto.setOwnerId(userId);

        Item updatedItem = itemRepository.update(ItemMapper.toItem(itemDto));

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto get(Integer itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId).get());
    }

    @Override
    public Collection<ItemDto> getByOwner(Integer userId) {
        return itemRepository.getByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


}