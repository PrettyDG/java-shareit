package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Integer userId, NewItemRequest request);

    List<ItemRequestDto> get(Integer userId);

    List<ItemRequestDto> getAll(Integer userId);

    ItemRequestDto findById(Integer requestId);

}