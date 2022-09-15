package ru.practicum.shareit.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AuthFailedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    User userBooker;
    Item item;
    ItemCreatingDto itemCreatingDto;
    ItemServiceImpl itemService;

    @BeforeEach
    void BeforeEach() {
        userBooker = new User(1L, "Serg", "latarho@gmail.com");
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, 1L);
        itemCreatingDto = new ItemCreatingDto("Это DTO вещь номер один", "Это описание DTO вещи номер один",
                true, 1L);
        itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, bookingRepository);
    }

    @Test
    void checkCreateItemPositive() throws UserNotFoundException, ValidationException {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDtoResult = itemService.create(1L, itemCreatingDto);

        assertThat(itemDtoResult.getId(), equalTo(item.getId()));
        assertThat(itemDtoResult.getName(), equalTo(item.getName()));
        assertThat(itemDtoResult.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoResult.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDtoResult.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    void checkCreateItemUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> itemService.create(1L, itemCreatingDto));
    }

    @Test
    void checkUpdateItemPositive() throws ValidationException, AuthFailedException {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDtoResult = itemService.update(1L, 1L, itemCreatingDto);

        assertThat(itemDtoResult.getId(), equalTo(item.getId()));
        assertThat(itemDtoResult.getName(), equalTo(item.getName()));
        assertThat(itemDtoResult.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoResult.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDtoResult.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    void checkUpdateItemDoesntBelongToUserPositive() {
        Item itemOther = new Item(2L, "Это вещь номер два", "Это описание вещи номер два", true,
                2L, 2L);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemOther));

        assertThrows(AuthFailedException.class, () -> itemService.update(1L, 1L, itemCreatingDto));
    }

    @Test
    void checkGetItemByIdPositive() throws UserNotFoundException, ItemNotFoundException {
        Item item = new Item(1L, "Это вещь номер один c запросом", "Это описание вещи номер один с запросом",
                true, 1L, 2L);
        ItemWithCommentDto itemWithCommentDto = new ItemWithCommentDto(1L, "Это вещь номер один c запросом",
                "Это описание вещи номер один с запросом", true, 2L, null, null, null);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemWithCommentDto itemDtoResult = itemService.getById(1L, 1L);

        assertThat(itemDtoResult.getId(), equalTo(itemWithCommentDto.getId()));
        assertThat(itemDtoResult.getName(), equalTo(itemWithCommentDto.getName()));
        assertThat(itemDtoResult.getDescription(), equalTo(itemWithCommentDto.getDescription()));
        assertThat(itemDtoResult.getAvailable(), equalTo(itemWithCommentDto.getAvailable()));
        assertThat(itemDtoResult.getRequestId(), equalTo(itemWithCommentDto.getRequestId()));
    }

    @Test
    void checkGetItemByIdUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void checkGetItemByIdItemNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));

        assertThrows(ItemNotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void checkGetAllItemsWithPaginationNoUserException() {
        assertThrows(UserNotFoundException.class, () -> itemService.getAllWithPagination(1L, 0, 5));
    }

    @Test
    void checkSearchItemByTextPositive() throws UserNotFoundException {
        Item itemDtoOne = new Item(1L, "Это DTO вещь номер один", "Это описание DTO вещи номер один",
                true, 1L, 2L);
        Item itemDtoTwo = new Item(1L, "Это DTO вещь номер два", "Это описание DTO вещи номер два",
                false, 2L, 2L);
        List<Item> listItems = new ArrayList<>();
        listItems.add(itemDtoOne);
        listItems.add(itemDtoTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.search(anyString(), any()))
                .thenReturn(listItems);
        List<ItemDto> itemDtoResult = itemService.searchByTextWithPagination(1L, "Item", 0, 5);

        assertThat(itemDtoResult.get(0).getId(), equalTo(itemDtoOne.getId()));
        assertThat(itemDtoResult.get(0).getName(), equalTo(itemDtoOne.getName()));
        assertThat(itemDtoResult.get(0).getDescription(), equalTo(itemDtoOne.getDescription()));
        assertThat(itemDtoResult.get(0).getAvailable(), equalTo(itemDtoOne.getAvailable()));
        assertThat(itemDtoResult.get(0).getRequestId(), equalTo(itemDtoOne.getRequestId()));
        assertThat(itemDtoResult.get(1).getId(), equalTo(itemDtoTwo.getId()));
        assertThat(itemDtoResult.get(1).getName(), equalTo(itemDtoTwo.getName()));
        assertThat(itemDtoResult.get(1).getDescription(), equalTo(itemDtoTwo.getDescription()));
        assertThat(itemDtoResult.get(1).getAvailable(), equalTo(itemDtoTwo.getAvailable()));
        assertThat(itemDtoResult.get(1).getRequestId(), equalTo(itemDtoTwo.getRequestId()));
    }

    @Test
    void checkSearchItemsNoUserException() {
        assertThrows(UserNotFoundException.class, () -> itemService.searchByTextWithPagination(1L, "Item", 0, 5));
    }

    @Test
    void checkCreateCommentPositive() throws UserNotFoundException, ValidationException {
        Item itemDto = new Item(1L, "Это DTO вещь номер один", "Это описание DTO вещи номер один",
                true, 1L, 2L);
        CommentCreatingDto commentCreatingDto = new CommentCreatingDto("Комментарий номер один");
        LocalDate created = LocalDate.now().plusDays(2);
        CommentDto commentDto = new CommentDto(1L, "Комментарий номер один", itemDto, "Serg", created);
        Comment comment = new Comment(1L, "Комментарий номер один", itemDto, userBooker, created);
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        Booking booking = new Booking(1L, start, end, itemDto, userBooker, BookingStatus.WAITING);
        List<Booking> listBooking = new ArrayList<>();
        listBooking.add(booking);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemDto));
        Mockito
                .when(bookingRepository.findForItem(anyLong(), anyLong()))
                .thenReturn(listBooking);
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);
        CommentDto commentResult = itemService.createComment(1L, 1L, commentCreatingDto);

        assertThat(commentResult.getId(), equalTo(commentDto.getId()));
        assertThat(commentResult.getText(), equalTo(commentDto.getText()));
        assertThat(commentResult.getCreated(), equalTo((commentDto.getCreated())));
        assertThat(commentResult.getAuthorName(), equalTo((commentDto.getAuthorName())));
    }
}