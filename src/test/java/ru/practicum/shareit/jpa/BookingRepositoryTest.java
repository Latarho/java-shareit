package ru.practicum.shareit.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    User userOwner;
    User userBooker;
    LocalDateTime startFirst;
    LocalDateTime endFirst;
    LocalDateTime startSecond;
    LocalDateTime endSecond;
    Item item;
    Booking BookingOne;
    Booking BookingTwo;

    @BeforeEach
    void BeforeEach() {
        userOwner = new User(1L, "userOwner", "latarho@gmail.com");
        userBooker = new User(2L, "userBooker", "latarho1@gmail.com");
        startFirst = LocalDateTime.now().plusDays(2L);
        endFirst = LocalDateTime.now().plusDays(4L);
        startSecond = LocalDateTime.now().plusDays(1L);
        endSecond = LocalDateTime.now().plusDays(3L);
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один", true,
                1L, null);
        BookingOne = new Booking(1L, startFirst, endFirst, item, userBooker, BookingStatus.WAITING);
        BookingTwo = new Booking(2L, startSecond, endSecond, item, userBooker, BookingStatus.WAITING);
    }

    @Test
    void checkFindBookingListForUserOrderByStartDesc() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idOrderByStartDesc(2L,
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);
        listToCompare.add(BookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithEndDateBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(2L,
                endSecond.plusHours(10), PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithStartDateAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(2L,
                startSecond.plusHours(5), PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithBookingStatusWaiting() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(2L,
                BookingStatus.WAITING, PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository
                .findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(2L, startSecond.plusHours(7),
                        endSecond.minusHours(5), PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerAllBookingStatuses() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);
        Booking BookingThreed = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(BookingThreed);

        List<Booking> foundBookings = bookingRepository.findForOwnerAllStatus(userOwner.getId(),
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);
        listToCompare.add(BookingTwo);
        listToCompare.add(BookingThreed);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerWaitingBookingStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);
        Booking BookingThreed = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(BookingThreed);

        List<Booking> foundBookings = bookingRepository.findForOwnerStatus(userOwner.getId(), BookingStatus.WAITING,
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerApprovedBookingStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);
        Booking BookingThreed = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(BookingThreed);

        List<Booking> foundBookings = bookingRepository.findForOwnerStatus(userOwner.getId(), BookingStatus.APPROVED,
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerPast() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerPast(userOwner.getId(), endSecond.plusHours(7),
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerFuture() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerFuture(userOwner.getId(), startSecond.plusHours(7),
                PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerCurrent() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(BookingOne);
        BookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(BookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerCurrent(userOwner.getId(), startFirst.plusDays(2),
                endSecond.minusDays(1), PageRequest.of(0, 5));
        List<Booking> listToCompare = new ArrayList<>();
        listToCompare.add(BookingOne);
        listToCompare.add(BookingTwo);
        
        assertThat(foundBookings, is(equalTo(listToCompare)));
    }
}