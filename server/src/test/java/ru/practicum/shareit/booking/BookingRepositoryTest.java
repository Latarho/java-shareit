package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
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

    private User userOwner;
    private User userBooker;
    private LocalDateTime startFirst;
    private LocalDateTime endFirst;
    private LocalDateTime startSecond;
    private LocalDateTime endSecond;
    private Item item;
    private Booking bookingOne;
    private Booking bookingTwo;

    @BeforeEach
    void beforeEach() {
        userOwner = new User(1L, "userOwner", "latarho@gmail.com");
        userBooker = new User(2L, "userBooker", "latarho1@gmail.com");
        startFirst = LocalDateTime.now().plusDays(2L);
        endFirst = LocalDateTime.now().plusDays(4L);
        startSecond = LocalDateTime.now().plusDays(1L);
        endSecond = LocalDateTime.now().plusDays(3L);
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один", true,
                1L, null);
        bookingOne = new Booking(1L, startFirst, endFirst, item, userBooker, BookingStatus.WAITING);
        bookingTwo = new Booking(2L, startSecond, endSecond, item, userBooker, BookingStatus.WAITING);
    }

    @Test
    void checkFindBookingListForUserOrderByStartDesc() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idOrderByStartDesc(2L, Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne, bookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithEndDateBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(2L,
                endSecond.plusHours(10), Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithStartDateAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(2L,
                startSecond.plusHours(5), Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithBookingStatusWaiting() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(2L,
                BookingStatus.WAITING, Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForUserOrderByStartDescWithStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository
                .findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(2L, startSecond.plusHours(7),
                        endSecond.minusHours(5), Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerAllBookingStatuses() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);
        Booking bookingThree = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(bookingThree);

        List<Booking> foundBookings = bookingRepository.findForOwnerAllStatus(userOwner.getId(), Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne, bookingTwo, bookingThree);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerWaitingBookingStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);
        Booking bookingThree = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(bookingThree);

        List<Booking> foundBookings = bookingRepository.findForOwnerStatus(userOwner.getId(), BookingStatus.WAITING,
                Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerApprovedBookingStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);
        Booking bookingThree = new Booking(3L, startSecond, endSecond, item, userBooker, BookingStatus.REJECTED);
        bookingRepository.save(bookingThree);

        List<Booking> foundBookings = bookingRepository.findForOwnerStatus(userOwner.getId(), BookingStatus.APPROVED,
                Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerPast() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerPast(userOwner.getId(), endSecond.plusHours(7),
                Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingTwo);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerFuture() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerFuture(userOwner.getId(), startSecond.plusHours(7),
                Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne);

        assertThat(foundBookings, is(equalTo(listToCompare)));
    }

    @Test
    void checkFindBookingListForOwnerCurrent() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);

        itemRepository.save(item);

        bookingRepository.save(bookingOne);
        bookingTwo.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(bookingTwo);

        List<Booking> foundBookings = bookingRepository.findForOwnerCurrent(userOwner.getId(), startFirst.plusDays(2),
                endSecond.minusDays(1), Pageable.unpaged());
        List<Booking> listToCompare = List.of(bookingOne, bookingTwo);
        assertThat(foundBookings, is(equalTo(listToCompare)));
    }
}