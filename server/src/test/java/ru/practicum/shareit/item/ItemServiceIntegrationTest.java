package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreatingDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void checkGetAllWithPaginationIntegrationPositive() {
        User userBooker = new User(1L, "Serg", "latarho@gmail.com");
        userRepository.saveAndFlush(userBooker);
        User userOwner = new User(2L, "Latarho", "latarho1@gmail.com");
        userRepository.saveAndFlush(userOwner);
        Item item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, 1L);
        itemRepository.saveAndFlush(item);
        Booking bookingOne = new Booking(1L, LocalDateTime.now().minus(2, ChronoUnit.DAYS),
                LocalDateTime.now().minus(1, ChronoUnit.DAYS), item, userBooker, BookingStatus.APPROVED);
        bookingRepository.saveAndFlush(bookingOne);
        Booking bookingTwo = new Booking(2L, LocalDateTime.now().plus(2, ChronoUnit.DAYS),
                LocalDateTime.now().plus(3, ChronoUnit.DAYS), item, userBooker, BookingStatus.APPROVED);
        bookingRepository.saveAndFlush(bookingTwo);
        List<ItemWithCommentDto> items = itemService.getAllWithPagination(userBooker.getId(), 0, 5);

        assertFalse(items.isEmpty());
        assertThat(item.getName(), equalTo(items.get(0).getName()));
    }

    @Test
    void checkCreateCommentIntegrationPositive() throws ValidationException {
        User userBooker = new User(1L, "Serg", "latarho@gmail.com");
        userRepository.saveAndFlush(userBooker);
        User userOwner = new User(2L, "Latarho", "latarho1@gmail.com");
        userRepository.saveAndFlush(userOwner);
        Item item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 1L, 1L);
        itemRepository.saveAndFlush(item);
        Booking bookingOne = new Booking(1L, LocalDateTime.now().minus(2, ChronoUnit.DAYS),
                LocalDateTime.now().minus(1, ChronoUnit.DAYS), item, userBooker, BookingStatus.APPROVED);
        bookingRepository.saveAndFlush(bookingOne);
        CommentCreatingDto commentCreatingDto = new CommentCreatingDto("Это отзыв номер один");
        itemService.createComment(userBooker.getId(), item.getId(), commentCreatingDto);

        List<Comment> comments = commentRepository.findAll();
        assertFalse(comments.isEmpty());
        assertThat(commentCreatingDto.getText(), equalTo(comments.get(0).getText()));
    }
}