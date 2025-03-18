package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

class ItemRequestServiceImplTest {

    private ItemRequestRepository repository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestServiceImpl service;

    private ItemRequest itemRequest;
    private User requestor;
    private NewItemRequest newItemRequest;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ItemRequestRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        service = new ItemRequestServiceImpl(repository, userRepository, itemRepository);

        requestor = new User(1, "User", "user@example.com");

        itemRequest = new ItemRequest(
                1,
                "Need a laptop",
                requestor,
                LocalDateTime.now()
        );

        newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Need a laptop");
    }

    @Test
    void addRequest_whenValidData_thenReturnRequestDto() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(requestor));
        Mockito.when(repository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto result = service.addRequest(1, newItemRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(result.getRequestor().getId()).isEqualTo(requestor.getId());
    }

    @Test
    void getRequests_whenValidUser_thenReturnListOfRequests() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(requestor));
        Mockito.when(repository.findAllByRequestorId(anyInt(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = service.get(1);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void getAllRequests_whenValidUser_thenReturnListOfRequests() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(requestor));
        Mockito.when(repository.findByRequestorIdNot(anyInt(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = service.getAll(1);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void findById_whenValidId_thenReturnRequestDto() {
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(anyInt()))
                .thenReturn(List.of());

        ItemRequestDto result = service.findById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void findById_whenInvalidId_thenThrowNotFoundException() {
        Mockito.when(repository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(1));
    }
}
