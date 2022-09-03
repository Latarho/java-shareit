package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(Long userId, BookingCreatingDto bookingCreatingDto) throws ItemNotFoundException,
            UserNotFoundException, ValidationException {
        Booking booking = BookingMapper.toBookingCreate(bookingCreatingDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            if (itemRepository.findById(booking.getItem().getId()).isEmpty()) {
                throw new ItemNotFoundException("Отсутствует вещь id: " + booking.getItem().getId());
            } else {
                if (booking.getStart().isBefore(LocalDateTime.now())
                        || booking.getEnd().isBefore(LocalDateTime.now())
                        || booking.getStart().isAfter(booking.getEnd())) {
                    throw new ValidationException("Booking date is incorrect");
                } else {
                    if (!itemRepository.findById(booking.getItem().getId()).orElseThrow().getAvailable()) {
                        throw new ValidationException("Вещь недоступна для бронирования id: "
                                                      + booking.getItem().getId());
                    } else {
                        if ((itemRepository.findById(booking.getItem().getId()).get().getOwnerId()).equals(userId)) {
                            throw new ItemNotFoundException("Вещь недоступна для бронирования для пользователя id: "
                                                            + userId);
                        } else {
                            booking.setStatus(BookingStatus.WAITING);
                            booking.setBooker(userRepository.findById(userId).get());
                            booking.setItem(itemRepository.findById(booking.getItem().getId()).orElseThrow());
                            return BookingMapper.toBookingDto(bookingRepository.save(booking));
                        }
                    }
                }
            }
        }
    }

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
                    throw new ItemNotBelongsToUserException("Вещь недоступна для бронирования для пользователя id: "
                                                            + userId);
                } else {
                    Booking booking = bookingRepository.findById(bookingId).get();
                    booking.setBooker(userRepository.findById(bookerId).orElseThrow());
                    booking.setItem(itemRepository.findById(booking.getItem().getId()).orElseThrow());
                    return BookingMapper.toBookingDto(booking);
                }
            }
        }
    }

    @Override
    public List<BookingDto> getAllBookingsForRequester(Long userId, State state) throws UserNotFoundException,
            UnsupportedStatusException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<Booking> foundBookings;
            switch (state) {
                case ALL:
                    foundBookings = bookingRepository.findByBooker_idOrderByStartDesc(userId);
                    break;
                case WAITING:
                    foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                            BookingStatus.WAITING);
                    break;
                case REJECTED:
                    foundBookings = bookingRepository.findByBooker_idAndStatusOrderByStartDesc(userId,
                            BookingStatus.REJECTED);
                    break;
                case PAST:
                    foundBookings = bookingRepository.findByBooker_idAndEndBeforeOrderByStartDesc(userId,
                            LocalDateTime.now());
                    break;
                case FUTURE:
                    foundBookings = bookingRepository.findByBooker_idAndStartAfterOrderByStartDesc(userId,
                            LocalDateTime.now());
                    break;
                case CURRENT:
                    foundBookings = bookingRepository.findByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
                    break;
                default:
                    throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
            List<BookingDto> listBookingDto = new ArrayList<>();
            for (Booking booking : foundBookings) {
                listBookingDto.add(BookingMapper.toBookingDto(booking));
            }
            return listBookingDto;
        }
    }

    @Override
    public List<BookingDto> getAllBookingsForOwner(Long userId, State state) throws UserNotFoundException,
            UnsupportedStatusException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            List<Booking> foundBookings;
            switch (state) {
                case ALL:
                    foundBookings = bookingRepository.findForOwnerAllStatus(userId);
                    break;
                case WAITING:
                    foundBookings = bookingRepository.findForOwnerStatus(userId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    foundBookings = bookingRepository.findForOwnerStatus(userId, BookingStatus.REJECTED);
                    break;
                case PAST:
                    foundBookings = bookingRepository.findForOwnerPast(userId, LocalDateTime.now());
                    break;
                case FUTURE:
                    foundBookings = bookingRepository.findForOwnerFuture(userId, LocalDateTime.now());
                    break;
                case CURRENT:
                    foundBookings = bookingRepository.findForOwnerCurrent(userId, LocalDateTime.now(),
                            LocalDateTime.now());
                    break;
                default:
                    throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
            List<BookingDto> listBookingDto = new ArrayList<>();
            for (Booking booking : foundBookings) {
                listBookingDto.add(BookingMapper.toBookingDto(booking));
            }
            return listBookingDto;
        }
    }

    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) throws UserNotFoundException,
            BookingNotFoundException, ItemNotBelongsToUserException, ValidationException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Отсутствует пользователь id: " + userId);
        } else {
            if (bookingRepository.findById(bookingId).isEmpty()) {
                throw new BookingNotFoundException("Отсутствует бронирование id: " + bookingId);
            } else {
                Long itemId = bookingRepository.findById(bookingId).get().getItem().getId();
                if (!(itemRepository.findById(itemId).orElseThrow().getOwnerId().equals(userId))) {
                    throw new ItemNotBelongsToUserException("Вещь недоступна для бронирования для пользователя id: "
                                                            + userId);
                } else {
                    if (bookingRepository.findById(bookingId).orElseThrow().getStatus().equals(BookingStatus.APPROVED)) {
                        throw new ValidationException("Бронирование со статусом Approved не может быть обновлено");
                    } else {
                        Booking foundBooking = bookingRepository.findById(bookingId).get();
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