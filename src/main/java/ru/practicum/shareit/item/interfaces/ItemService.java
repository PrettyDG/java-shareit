package ru.practicum.shareit.item.interfaces;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.*;

import java.util.Collection;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDtoResponse create(ItemDtoRequest itemDtoRequest, Integer userId);

    ItemDtoResponse update(Integer itemId, ItemDtoRequest itemDtoRequest, Integer userId);

    ItemDto get(Integer itemId, Integer userId);

    Collection<ItemDtoResponse> getByOwner(Integer userId);

    Collection<ItemDtoResponse> search(String text);

    @Transactional
    CommentDtoResponse addComment(int userId, int itemId, CommentDtoRequest commentDtoRequest);
}
