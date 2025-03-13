package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto addRequest(Integer userId, NewItemRequest request) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Юзер не найден"));
        ItemRequest itemRequest = ItemRequestMapper.fromNewItemRequest(request, requestor, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(repository.save(itemRequest));

    }

    public List<ItemRequestDto> get(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Юзер не найден"));
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        return repository.findAllByRequestorId(userId, sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();

    }

    public List<ItemRequestDto> getAll(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Юзер не найден"));
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        return repository.findByRequestorIdNot(userId, sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();

    }

    public ItemRequestDto findById(Integer requestId) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Реквест не найден")));
        log.info("All items in database:");
        itemRepository.findAll().forEach(item ->
                log.info("Item: id={}, name={}, requestId={}",
                        item.getId(),
                        item.getName(),
                        item.getRequest() != null ? item.getRequest().getId() : "NULL"
                )
        );
        itemRequestDto.setItems(itemRepository.findByRequestId(requestId));
        return itemRequestDto;
    }
}