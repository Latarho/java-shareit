package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    private User userBooker;
    private Item item;
    LocalDateTime start;
    LocalDateTime end;
    BookingServiceImpl bookingService;

    @BeforeEach
    void beforeEach() {
        userBooker = new User(1L, "Serg", "latarho@gmail.com");
        item = new Item(1L, "Это вещь номер один", "Это описание вещи номер один",
                true, 2L, null);
        start = LocalDateTime.now().plusDays(3);
        end = LocalDateTime.now().plusDays(6);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void checkCreateBookingPositive() throws UserNotFoundException, ValidationException, ItemNotFoundException {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        BookingDto bookingDto = new BookingDto(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto bookingDtoResult = bookingService.create(1L, bookingCreatingDto);

        assertThat(bookingDtoResult.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoResult.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoResult.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoResult.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void checkCreateBookingUserNotFoundException() {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);

        assertThrows(UserNotFoundException.class, () -> bookingService.create(1L, bookingCreatingDto));
    }

    @Test
    void checkCreateBookingItemNotFound() {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(1L, bookingCreatingDto));
    }

    @Test
    void checkCreateBookingDateIsIncorrect() {
        LocalDateTime startIncorrect = LocalDateTime.now().plusDays(2);
        LocalDateTime endIncorrect = LocalDateTime.now().minusDays(2);
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(startIncorrect, endIncorrect, 1L);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingCreatingDto));
    }

    @Test
    void checkCreateBookingItemNotAvailable() {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);
        item.setAvailable(false);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingCreatingDto));
    }

    @Test
    void checkCreateBookingItemCantBeBookedByUser() {
        BookingCreatingDto bookingCreatingDto = new BookingCreatingDto(start, end, 1L);
        item.setOwnerId(1L);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(1L, bookingCreatingDto));
    }

    @Test
    void checkUpdateBookingToApprovedPositive() throws UserNotFoundException, BookingNotFoundException,
            ValidationException, ItemNotBelongsToUserException {
        item.setOwnerId(1L);
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingDto = new Booking(1L, start, end, item, userBooker, BookingStatus.APPROVED);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingDto);
        BookingDto bookingDtoResult = bookingService.update(1L, 1L, true);

        assertThat(bookingDtoResult.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoResult.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoResult.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoResult.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void checkUpdateBookingToRejectedPositive() throws UserNotFoundException, BookingNotFoundException,
            ValidationException, ItemNotBelongsToUserException {
        item.setOwnerId(1L);
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingDto = new Booking(1L, start, end, item, userBooker, BookingStatus.REJECTED);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingDto);
        BookingDto bookingDtoResult = bookingService.update(1L, 1L, false);

        assertThat(bookingDtoResult.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoResult.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoResult.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoResult.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void checkUpdateBookingUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void checkUpdateBookingBookingNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));

        assertThrows(BookingNotFoundException.class, () -> bookingService.update(1L, 2L, true));
    }

    @Test
    void checkUpdateBookingItemNotBelongsToUserException() {
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(ItemNotBelongsToUserException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void checkUpdateBookingStatusCantBeChangedException() {
        item.setOwnerId(1L);
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.APPROVED);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(ValidationException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void checkGetBookingByIdPositive() throws UserNotFoundException, BookingNotFoundException,
            ItemNotBelongsToUserException {
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        BookingDto bookingDto = new BookingDto(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        BookingDto bookingDtoResult = bookingService.getById(1L, 1L);

        assertThat(bookingDtoResult.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoResult.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoResult.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoResult.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void checkGetBookingByIdUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> bookingService.getById(2L, 1L));
    }

    @Test
    void checkGetBookingByIdBookingNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    void checkGetBookingByIdItemNotBelongsToUserException() {
        item.setOwnerId(1L);
        Booking booking = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ItemNotBelongsToUserException.class, () -> bookingService.getById(2L, 1L));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationAllStatuses() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.APPROVED);
        Booking bookingThree = new Booking(3L, start.minusDays(1), end, item, userBooker, BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingThree, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L, State.ALL,
                0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingThree.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingThree.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingThree.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingThree.getStatus()));
        assertThat(bookingDtoResult.get(2).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(2).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(2).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(2).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationWaitingStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.WAITING);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L,
                State.WAITING, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationRejectedStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.REJECTED);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L,
                State.REJECTED, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationPastStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start.minusDays(5), end.minusDays(6), item, userBooker,
                BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.minusDays(10), end.minusDays(7), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L,
                State.PAST, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationFutureStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.plusDays(2), end.plusDays(2), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L,
                State.FUTURE, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationCurrentStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start.minusDays(3), end, item, userBooker,
                BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.minusDays(4), end.plusDays(1), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(),
                        any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForRequesterWithPagination(1L,
                State.CURRENT, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForRequesterWithPaginationUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                bookingService.getAllBookingsForRequesterWithPagination(1L, State.ALL, 0, 5));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                bookingService.getAllBookingsForOwnerWithPagination(1L, State.ALL, 0, 5));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationAllStatuses() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.APPROVED);
        Booking bookingThree = new Booking(3L, start.minusDays(1), end, item, userBooker, BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingThree, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerAllStatus(anyLong(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L, State.ALL,
                0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingThree.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingThree.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingThree.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingThree.getStatus()));
        assertThat(bookingDtoResult.get(2).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(2).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(2).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(2).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationWaitingStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.WAITING);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.WAITING);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerStatus(anyLong(), any(), any()))
                .thenReturn(bookingList);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L,
                State.WAITING, 0, 5);
        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationRejectedStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.REJECTED);
        Booking bookingTwo = new Booking(2L, start.minusDays(2), end, item, userBooker, BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerStatus(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L,
                State.REJECTED, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationPastStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start.minusDays(4), end.minusDays(6), item, userBooker,
                BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.minusDays(9), end.minusDays(7), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerPast(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L,
                State.PAST, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationFutureStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start, end, item, userBooker, BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.plusDays(3), end.plusDays(3), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerFuture(anyLong(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L,
                State.FUTURE, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }

    @Test
    void checkGetBookingsForOwnerWithPaginationCurrentStatus() throws UserNotFoundException {
        Booking bookingOne = new Booking(1L, start.minusDays(3), end, item, userBooker, BookingStatus.APPROVED);
        Booking bookingTwo = new Booking(2L, start.minusDays(5), end.plusDays(1), item, userBooker,
                BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(bookingOne, bookingTwo);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBooker));
        Mockito
                .when(bookingRepository.findForOwnerCurrent(anyLong(), any(), any(), any()))
                .thenReturn(bookingList);
        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsForOwnerWithPagination(1L,
                State.CURRENT, 0, 5);

        assertThat(bookingDtoResult.get(0).getId(), equalTo(bookingOne.getId()));
        assertThat(bookingDtoResult.get(0).getStart(), equalTo(bookingOne.getStart()));
        assertThat(bookingDtoResult.get(0).getEnd(), equalTo(bookingOne.getEnd()));
        assertThat(bookingDtoResult.get(0).getStatus(), equalTo(bookingOne.getStatus()));
        assertThat(bookingDtoResult.get(1).getId(), equalTo(bookingTwo.getId()));
        assertThat(bookingDtoResult.get(1).getStart(), equalTo(bookingTwo.getStart()));
        assertThat(bookingDtoResult.get(1).getEnd(), equalTo(bookingTwo.getEnd()));
        assertThat(bookingDtoResult.get(1).getStatus(), equalTo(bookingTwo.getStatus()));
    }
}