package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDtoResponse create(ItemDtoRequest itemDtoRequest, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id - " + userId));

        Item item = ItemMapper.dtoToItem(itemDtoRequest, user, null);

        if (itemDtoRequest.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDtoRequest.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Реквест не найден"));

            item.setRequest(itemRequest);
            itemRepository.save(item);

            return ItemMapper.toItemDtoWithRequest(item);
        }

        return ItemMapper.itemToDtoResponse(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse update(Integer itemId, ItemDtoRequest itemDtoRequest, Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id - " + userId);
        } else if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Не найден item с id - " + itemId);
        }

        Item oldItem = itemRepository.findById(itemId).get();

        if (!oldItem.getUser().getId().equals(userId)) {
            throw new ValidationException("Item с id - " + itemId + " не принаджелит пользователю - " + userId);
        }
        if (itemDtoRequest.getName() == null || itemDtoRequest.getName().isEmpty()) {
            itemDtoRequest.setName(oldItem.getName());
        }
        if (itemDtoRequest.getDescription() == null || itemDtoRequest.getDescription().isEmpty()) {
            itemDtoRequest.setDescription(oldItem.getDescription());
        }

        Item updatedItem = itemRepository.save(ItemMapper.dtoToItem(itemDtoRequest, userRepository.findById(userId).get(), itemId));

        return ItemMapper.itemToDtoResponse(updatedItem);
    }

    @Override
    public ItemDto get(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не найден item с id - " + itemId));
        ItemDtoResponse itemDtoResponse = ItemMapper.itemToDtoResponse(item);

        List<CommentDtoResponse> commentDtoResponseList = commentRepository.getAllCommentsByItemId(itemId).stream()
                .map(CommentMapper::commentToDtoResponse)
                .toList();

        Booking nextBooking = getNextBooking(userId);
        Booking lastBooking = getLastBooking(userId);

        return ItemMapper.toItemDto(userId, itemDtoResponse, commentDtoResponseList,
                BookingMapper.toBookingDto(nextBooking),
                BookingMapper.toBookingDto(lastBooking));
    }

    @Override
    public Collection<ItemDtoResponse> getByOwner(Integer userId) {
        List<Item> items = itemRepository.findAllItemsByUserOrderByIdAsc(userRepository.findById(userId).orElseThrow());

        return items.stream()
                .map(ItemMapper::itemToDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> search(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::itemToDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(int userId, int itemId, CommentDtoRequest commentDtoRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден User с id - " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Не найден Item с id - " + itemId));
        Set<Integer> itemIds = new HashSet<>();
        itemIds.add(itemId);

        List<Booking> bookings = bookingRepository.findAllBookingsByItemIdInOrderByStartDesc(itemIds);

        if (bookings.isEmpty()) {
            throw new ValidationException("Для предмета с id: " + itemId + " бронирования не было.");
        }

        boolean hasCompletedBooking = bookingRepository.findById(itemId).stream()
                .anyMatch(booking -> userId == booking.getBooker().getId()
                        && booking.getEnd().isBefore(LocalDateTime.now().plusHours(4)));

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь с id: " + userId +
                    " не бронировал предмет с id: " + itemId +
                    " или срок бронирования не истек!");
        }

        Comment comment = CommentMapper.dtoToComment(commentDtoRequest, item, user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.commentToDtoResponse(commentRepository.save(comment));
    }

    private Booking getNextBooking(int userId) {
        List<Booking> bookings = bookingRepository
                .findAllBookingsByItemUserIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());

        Booking booking = new Booking();

        if (bookings != null && !bookings.isEmpty()) {
            booking = bookings.getFirst();
        }
        return booking;
    }

    private Booking getLastBooking(int userId) {
        List<Booking> bookings = bookingRepository
                .findAllBookingsByItemUserIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

        Booking booking = new Booking();

        if (bookings != null && !bookings.isEmpty()) {
            booking = bookings.getFirst();
        }
        return booking;
    }
}