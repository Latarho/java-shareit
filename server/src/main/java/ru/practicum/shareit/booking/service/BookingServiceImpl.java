package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    /**
     * Создание нового бронирования
     * @param userId идентификатор пользователя, который бронирует вещь
     * @param bookingCreatingDto объект класса Booking
     * @return объект класса Booking (новое бронирование)
     */
    @Override
    public BookingDto create(Long userId, BookingCreatingDto bookingCreatingDto) throws ItemNotFoundException,
            UserNotFoundException, ValidationException {
        Booking booking = BookingMapper.toBookingCreate(bookingCreatingDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            Optional<Item> itemForBooking = itemRepository.findById(booking.getItem().getId());
            if (itemForBooking.isEmpty()) {
                throw new ItemNotFoundException("Отсутствует вещь id: " + booking.getItem().getId());
            } else {
                if (booking.getStart().isBefore(LocalDateTime.now())
                        || booking.getEnd().isBefore(LocalDateTime.now())
                        || booking.getStart().isAfter(booking.getEnd())) {
                    throw new ValidationException("Некоррректная дата бронирования");
                } else {
                    if (!itemForBooking.orElseThrow().getAvailable()) {
                        throw new ValidationException("Вещь недоступна для бронирования id: "
                                                      + booking.getItem().getId());
                    } else {
                        if ((itemForBooking.get().getOwnerId()).equals(userId)) {
                            throw new ItemNotFoundException("Вещь недоступна для бронирования для пользователя id: "
                                                            + userId);
                        } else {
                            booking.setStatus(BookingStatus.WAITING);
                            booking.setBooker(userRepository.findById(userId).get());
                            booking.setItem(itemForBooking.orElseThrow());
                            return BookingMapper.toBookingDto(bookingRepository.save(booking));
                        }
                    }
                }
            }
        }
    }

    /**
     * Получение брониирования по переданному id для пользователя
     * @param userId идентификатор пользователя
     * @param bookingId идентификатор бронирования
     * @return объект класса Booking соответствующий переданному id
     */
    @Override
    public BookingDto getById(Long userId, Long bookingId) throws ItemNotBelongsToUserException, UserNotFoundException,
            BookingNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            if (bookingRepository.findById(bookingId).isEmpty()) {
                throw new BookingNotFoundException("Отсутствует бронирование id: " + bookingId);
            } else {
                Long bookerId = bookingRepository.findById(bookingId).get().getBooker().getId();
                Item itemFromBooking = itemRepository
                        .findById(bookingRepository.findById(bookingId).get().getItem().getId()).orElseThrow();
                if (!(bookerId.equals(userId)) && !((itemFromBooking.getOwnerId()).equals(userId))) {
                    throw new ItemNotBelongsToUserException("Вещь не принадлежит пользователю id: " + userId);
                } else {
                    Booking booking = bookingRepository.findById(bookingId).get();
                    booking.setBooker(userRepository.findById(bookerId).orElseThrow());
                    booking.setItem(itemRepository.findById(booking.getItem().getId()).orElseThrow());
                    return BookingMapper.toBookingDto(booking);
                }
            }
        }
    }

    /**
     * Получение информации по всем бронированиям для пользователя
     * @param userId идентификатор пользователя
     * @param state статус бронирования
     * @param from количество результатов от 0 которые пропускаем
     * @param size количество результатов в ответе
     * @return список бронирований с определенным статусом для пользователя
     */
    @Override
    public List<BookingDto> getAllBookingsForRequesterWithPagination(Long userId, State state, Integer from,
                                                                     Integer size)
            throws UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<Booking> foundBookings = new ArrayList<>();
            switch (state) {
                case ALL:
                    foundBookings = bookingRepository.findByBooker_idOrderByStartDesc(userId,
                            PageRequest.of(from / size, size));
                    break;
                case WAITING:
                    foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                            BookingStatus.WAITING, PageRequest.of(from / size, size));
                    break;
                case REJECTED:
                    foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                            BookingStatus.REJECTED, PageRequest.of(from / size, size));
                    break;
                case PAST:
                    foundBookings = bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(userId,
                            LocalDateTime.now(), PageRequest.of(from / size, size));
                    break;
                case FUTURE:
                    foundBookings = bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), PageRequest.of(from / size, size));
                    break;
                case CURRENT:
                    foundBookings = bookingRepository.findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from / size, size));
                    break;
            }
            List<BookingDto> listBookingDto = new ArrayList<>();
            for (Booking booking : foundBookings) {
                listBookingDto.add(BookingMapper.toBookingDto(booking));
            }
            return listBookingDto;
        }
    }

    /**
     * Получение информации по всем бронированиям для владельца
     * @param userId идентификатор пользователя
     * @param state статус бронирования
     * @param from количество результатов от 0 которые пропускаем
     * @param size количество результатов в ответе
     * @return список бронирований с определенным статусом для владельца
     */
    @Override
    public List<BookingDto> getAllBookingsForOwnerWithPagination(Long userId, State state, Integer from, Integer size)
            throws UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<Booking> foundBookings = new ArrayList<>();
            switch (state) {
                case ALL:
                    foundBookings = bookingRepository.findForOwnerAllStatus(userId,
                            PageRequest.of(from / size, size));
                    break;
                case WAITING:
                    foundBookings = bookingRepository.findForOwnerStatus(userId, BookingStatus.WAITING,
                            PageRequest.of(from / size, size));
                    break;
                case REJECTED:
                    foundBookings = bookingRepository.findForOwnerStatus(userId, BookingStatus.REJECTED,
                            PageRequest.of(from / size, size));
                    break;
                case PAST:
                    foundBookings = bookingRepository.findForOwnerPast(userId, LocalDateTime.now(),
                            PageRequest.of(from / size, size));
                    break;
                case FUTURE:
                    foundBookings = bookingRepository.findForOwnerFuture(userId, LocalDateTime.now(),
                            PageRequest.of(from / size, size));
                    break;
                case CURRENT:
                    foundBookings = bookingRepository.findForOwnerCurrent(userId, LocalDateTime.now(),
                            LocalDateTime.now(), PageRequest.of(from / size, size));
                    break;
            }
            List<BookingDto> listBookingDto = new ArrayList<>();
            for (Booking booking : foundBookings) {
                listBookingDto.add(BookingMapper.toBookingDto(booking));
            }
            return listBookingDto;
       }
    }

    /**
     * Обновление существующего бронирования
     * @param userId идентификатор пользователя
     * @param bookingId идентификатор бронирования
     * @param approved Подтверждено true/false
     * @return объект класса Booking с обновленными данными
     */
    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) throws UserNotFoundException,
            BookingNotFoundException, ItemNotBelongsToUserException, ValidationException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            Optional<Booking> validBooking = bookingRepository.findById(bookingId);
            if (validBooking.isEmpty()) {
                throw new BookingNotFoundException("Отсутствует бронирование id: " + bookingId);
            } else {
                Long itemId = validBooking.get().getItem().getId();
                if (!(itemRepository.findById(itemId).orElseThrow().getOwnerId().equals(userId))) {
                    throw new ItemNotBelongsToUserException("Вещь не принадлежит пользователю id: " + userId);
                } else {
                    if (validBooking.orElseThrow().getStatus().equals(BookingStatus.APPROVED)) {
                        throw new ValidationException("Бронирование со статусом Approved не может быть обновлено");
                    } else {
                        Booking foundBooking = validBooking.get();
                        if (approved.equals(true)) {
                            foundBooking.setStatus(BookingStatus.APPROVED);
                        } else {
                            foundBooking.setStatus(BookingStatus.REJECTED);
                        }
                        return BookingMapper.toBookingDto(bookingRepository.save(foundBooking));
                    }
                }
            }
        }
    }
}