package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest fromNewItemRequest(NewItemRequest request, User requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }
}