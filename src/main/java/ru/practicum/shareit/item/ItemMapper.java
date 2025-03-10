package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public Item dtoToItem(ItemDtoRequest itemDtoRequest, User user, Integer itemId) {
        return Item.builder()
                .id(itemId)
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .user(user)
                .build();
    }

    public ItemDtoResponse itemToDtoResponse(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getUser())
                .available(item.getAvailable())
                .build();
    }

    public ItemDto toItemDto(int userId, ItemDtoResponse item, List<CommentDtoResponse> comments,
                             BookingDto nextBooking, BookingDto lastBooking) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .build();

        if (comments != null) {
            itemDto.setComments(comments);
        }

        if (item.getOwner().getId() == userId) {
            if (nextBooking != null) {
                itemDto.setNextBooking(nextBooking);
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(lastBooking);
            }
        }
        return itemDto;
    }
}