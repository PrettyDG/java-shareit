package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.*;

import java.util.Collection;

public interface ItemService {
    ItemDtoResponse create(ItemDtoRequest itemDtoRequest, Integer userId);

    ItemDtoResponse update(Integer itemId, ItemDtoRequest itemDtoRequest, Integer userId);

    ItemDto get(Integer itemId, Integer userId);

    Collection<ItemDtoResponse> getByOwner(Integer userId);

    Collection<ItemDtoResponse> search(String text);

    CommentDtoResponse addComment(int userId, int itemId, CommentDtoRequest commentDtoRequest);
}
