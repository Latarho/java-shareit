package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkGetAllBookingsForRequesterWithPaginationIntegrationPositive() {
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
        List<BookingDto> pastBooking = bookingService.getAllBookingsForRequesterWithPagination(userBooker.getId(),
                State.PAST,0, 5);
        List<BookingDto> futureBooking = bookingService.getAllBookingsForRequesterWithPagination(userBooker.getId(),
                State.FUTURE, 0, 5);

        assertFalse(pastBooking.isEmpty());
        assertThat(bookingOne.getId(), equalTo(pastBooking.get(0).getId()));
        assertFalse(futureBooking.isEmpty());
        assertThat(bookingTwo.getId(), equalTo(futureBooking.get(0).getId()));
    }

    @Test
    void checkGetAllBookingsForOwnerWithPaginationIntegrationPositive() {
        User userBooker = new User(1L, "Serg", "latarho@gmail.com");
        userRepository.saveAndFlush(userBooker);
        User userOwner = new User(2L, "Latarho", "latarho1@gmail.com");
        userRepository.saveAndFlush(userOwner);
        Item item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 2L, 1L);
        itemRepository.saveAndFlush(item);
        Booking bookingOne = new Booking(1L, LocalDateTime.now().minus(2, ChronoUnit.DAYS),
                LocalDateTime.now().minus(1, ChronoUnit.DAYS), item, userBooker, BookingStatus.APPROVED);
        bookingRepository.saveAndFlush(bookingOne);
        Booking bookingTwo = new Booking(2L, LocalDateTime.now().plus(2, ChronoUnit.DAYS),
                LocalDateTime.now().plus(3, ChronoUnit.DAYS), item, userBooker, BookingStatus.APPROVED);
        bookingRepository.saveAndFlush(bookingTwo);
        List<BookingDto> pastBooking = bookingService.getAllBookingsForOwnerWithPagination(userOwner.getId(),
                State.PAST,0, 5);
        List<BookingDto> futureBooking = bookingService.getAllBookingsForOwnerWithPagination(userOwner.getId(),
                State.FUTURE, 0, 5);

        assertFalse(pastBooking.isEmpty());
        assertThat(bookingOne.getId(), equalTo(pastBooking.get(0).getId()));
        assertFalse(futureBooking.isEmpty());
        assertThat(bookingTwo.getId(), equalTo(futureBooking.get(0).getId()));
    }
}