package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDtoRequest;
import ru.practicum.shareit.item.ItemDtoResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void dtoToItem_ShouldMapDtoToItem() {
        ItemDtoRequest dtoRequest = ItemDtoRequest.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        User user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        Integer itemId = 1;

        Item result = ItemMapper.dtoToItem(dtoRequest, user, itemId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo(dtoRequest.getName());
        assertThat(result.getDescription()).isEqualTo(dtoRequest.getDescription());
        assertThat(result.getAvailable()).isEqualTo(dtoRequest.getAvailable());
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void itemToDtoResponse_ShouldMapItemToDtoResponse() {
        User user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        UserDto userDto = UserDto.builder()
                .id(2)
                .name("Conor")
                .email("123@mail.com")
                .build();

        UserMapper.toUser(userDto);

        Item item = Item.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .user(user)
                .build();

        ItemDtoResponse result = ItemMapper.itemToDtoResponse(item);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(result.getOwner()).isEqualTo(item.getUser());
    }
}
