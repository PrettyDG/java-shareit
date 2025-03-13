package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .user(user)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Request Description")
                .requestor(user)
                .build();
    }

    @Test
    void create_ShouldCreateItem_WhenRequestExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDtoRequest request = ItemDtoRequest.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequest.getId())
                .build();

        ItemDtoResponse result = itemService.create(request, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(item.getName());
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemDtoRequest request = ItemDtoRequest.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.create(request, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Не найден пользователь с id - 99");
    }

    @Test
    void update_ShouldUpdateItem_WhenOwnerIsCorrect() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDtoRequest updateRequest = ItemDtoRequest.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDtoResponse result = itemService.update(item.getId(), updateRequest, user.getId());

        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {
        User otherUser = User.builder().id(2).build();

        userRepository.save(otherUser);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoRequest request = ItemDtoRequest.builder()
                .name("Test")
                .build();

        assertThatThrownBy(() -> itemService.update(item.getId(), request, otherUser.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Не найден пользователь с id - 2");
    }

    @Test
    void get_ShouldReturnItem_WhenExists() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.getAllCommentsByItemId(item.getId())).thenReturn(List.of());

        ItemDto result = itemService.get(item.getId(), user.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
    }

    @Test
    void search_ShouldReturnItems_WhenTextIsProvided() {
        when(itemRepository.search("Test")).thenReturn(List.of(item));

        List<ItemDtoResponse> result = itemService.search("Test").stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(item.getName());
    }

    @Test
    void addComment_ShouldThrowException_WhenNoBookingsExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllBookingsByItemIdInOrderByStartDesc(Set.of(item.getId()))).thenReturn(List.of());

        CommentDtoRequest request = CommentDtoRequest.builder()
                .text("Nice!")
                .build();

        assertThatThrownBy(() -> itemService.addComment(user.getId(), item.getId(), request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Для предмета с id: 1 бронирования не было.");
    }

    @Test
    void getByOwner_whenItemsExist_thenReturnItems() {
        int userId = 1;
        User user = new User(userId, "Test User", "test@example.com");
        Item item1 = new Item(1, "Item 1", "Description 1", true, user, null);
        Item item2 = new Item(2, "Item 2", "Description 2", true, user, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllItemsByUserOrderByIdAsc(user)).thenReturn(List.of(item1, item2));

        List<ItemDtoResponse> result = itemService.getByOwner(userId).stream().toList();

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());

        verify(userRepository).findById(userId);
        verify(itemRepository).findAllItemsByUserOrderByIdAsc(user);
    }
}
