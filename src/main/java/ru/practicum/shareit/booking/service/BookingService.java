package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.*;

import java.util.List;

public interface BookingService {
    // Создание бронирования по запросу пользователя
    BookingDto create(Long userId, BookingCreatingDto booking) throws ItemNotFoundException, UserNotFoundException,
            ValidationException;
    // Поиск бронирования по Id
        BookingDto getById(Long userId, Long bookingId) throws ItemNotBelongsToUserException, UserNotFoundException,
            BookingNotFoundException;
    // Получение информации по всем бронированиям для пользователя
    List<BookingDto> getAllBookingsForRequesterWithPagination(Long userId, State state, Integer from, Integer size)
            throws UserNotFoundException;
    // Получение информации по всем бронированиям для владельца
    List<BookingDto> getAllBookingsForOwnerWithPagination(Long userId, State state, Integer from, Integer size)
            throws UserNotFoundException;
    // Обновление информации о бронировании
    BookingDto update(Long userId, Long bookingId, Boolean approved) throws UserNotFoundException,
            BookingNotFoundException, ItemNotBelongsToUserException, ValidationException;
}