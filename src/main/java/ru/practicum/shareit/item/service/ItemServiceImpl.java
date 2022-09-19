package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AuthFailedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemCreatingDto itemCreatingDto) throws ValidationException, UserNotFoundException {
        Item item = ItemMapper.toItem(itemCreatingDto);
        if (userRepository.findById(userId).isPresent()) {
            item.setOwnerId(userId);
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        }
    }

    @Override
    public ItemWithCommentDto getById(Long userId, Long id) throws ItemNotFoundException, UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            if (itemRepository.findById(id).isEmpty()) {
                throw new ItemNotFoundException("Отсутствует вещь id: " + id);
            } else {
                List<CommentForItemDto> comments = new ArrayList<>();
                for (Comment comment : commentRepository.findByItem_id(id)) {
                    comments.add(CommentMapper.toCommentForItemDto(comment));
                }
                BookingItemDto lastBooking = (getLastBooking(id, userId) == null) ? null
                        : BookingMapper.toBookingDtoItem(Objects.requireNonNull(getLastBooking(id, userId)));
                BookingItemDto nextBooking = (getNextBooking(id, userId)) == null ? null
                        : BookingMapper.toBookingDtoItem(Objects.requireNonNull(getNextBooking(id, userId)));
                return ItemMapper.toItemDtoWithComment(itemRepository.findById(id).get(), comments, lastBooking,
                        nextBooking);
            }
        }
    }

    @Override
    public List<ItemWithCommentDto> getAllWithPagination(Long userId, Integer from, Integer size)
            throws UserNotFoundException {
        if (!userRepository.findById(userId).isPresent()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<Item> itemToReturn = new ArrayList<>();
            for (Item item : itemRepository.findAllByOrderByIdAsc(PageRequest.of(from / size, size))) {
                if (Objects.equals(item.getOwnerId(), userId)) {
                    itemToReturn.add(item);
                }
            }
            List<ItemWithCommentDto> listItemCommentDto = new ArrayList<>();
            for (Item item : itemToReturn) {
                List<CommentForItemDto> comments = new ArrayList<>();
                for (Comment comment : commentRepository.findByItem_id(item.getId())) {
                    comments.add(CommentMapper.toCommentForItemDto(comment));
                }
                BookingItemDto lastBooking = (getLastBooking(item.getId(), userId)) == null ? null
                        : BookingMapper.toBookingDtoItem(Objects.requireNonNull(getLastBooking(item.getId(), userId)));
                BookingItemDto nextBooking = (getNextBooking(item.getId(), userId)) == null ? null
                        : BookingMapper.toBookingDtoItem(Objects.requireNonNull(getNextBooking(item.getId(), userId)));
                listItemCommentDto.add(ItemMapper.toItemDtoWithComment(item, comments, lastBooking, nextBooking));
            }
            return listItemCommentDto;
        }
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemCreatingDto itemCreatingDto) throws ValidationException,
            AuthFailedException {
        Item item = ItemMapper.toItem(itemCreatingDto);
        Optional<Item> itemFromList = itemRepository.findById(id);
        userRepository.findById(userId);
        if (!Objects.equals(userId, itemFromList.get().getOwnerId())) {
            throw new AuthFailedException("Вещь больше не закреплена за пользователем id: " + userId);
        } else {
            item.setOwnerId(userId);
            item.setId(id);
            if (item.getName() == null) {
                item.setName(itemFromList.get().getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(itemFromList.get().getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(itemFromList.get().getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.save(item));
        }
    }

    @Override
    public List<ItemDto> searchByTextWithPagination(Long userId, String searchText, Integer from, Integer size)
            throws UserNotFoundException {
        if (userRepository.findById(userId).isPresent()) {
            if (searchText.isEmpty()) {
                return Collections.emptyList();
            } else {
                List<Item> foundItems = itemRepository.search(searchText, PageRequest.of(from / size, size));

                List<ItemDto> listItemDto = new ArrayList<>();
                if (foundItems == null) {
                    return null;
                } else {
                    for (Item item : foundItems) {
                        listItemDto.add(ItemMapper.toItemDto(item));
                    }
                    return listItemDto;
                }
            }
        } else {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        }
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentCreatingDto commentCreatingDto)
            throws UserNotFoundException, ValidationException {
        Comment comment = CommentMapper.toComment(commentCreatingDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            System.out.println(bookingRepository.findForItem(itemId, userId).size());
            if (bookingRepository.findForItem(itemId, userId).size() == 0) {
                throw new ValidationException("Пользователь не может оставить отзыв для данной вещи");
            } else {
                User user = userRepository.findById(userId).orElseThrow();
                Item item = itemRepository.findById(itemId).orElseThrow();
                comment.setItem(item);
                comment.setAuthor(user);
                comment.setCreated(LocalDate.now());
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            }
        }
    }

    private Booking getLastBooking(Long itemId, Long userId) {
        if (bookingRepository.getLastBooking(itemId, userId).size() == 0) {
            return null;
        } else {
            return bookingRepository.getLastBooking(itemId, userId).get(0);
        }
    }

    private Booking getNextBooking(Long itemId, Long userId) {
        if (bookingRepository.getNextBooking(itemId, userId).size() == 0) {
            return null;
        } else {
            return bookingRepository.getNextBooking(itemId, userId).get(0);
        }
    }
}