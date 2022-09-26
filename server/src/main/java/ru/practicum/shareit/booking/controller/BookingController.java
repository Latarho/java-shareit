package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.helpers.HeaderKey;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                             @RequestBody BookingCreatingDto bookingCreatingDto)
            throws ItemNotFoundException, UserNotFoundException, ValidationException {
        log.info("Получен запрос - создание нового бронирования: " + bookingCreatingDto.toString());
        return bookingService.create(userId, bookingCreatingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long bookingId,
                             @RequestParam Boolean approved)
            throws UserNotFoundException, BookingNotFoundException, ItemNotBelongsToUserException,
            ValidationException {
        log.info("Получен запрос - обновление информации о бронировании (статус) id: " + bookingId.toString());
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long bookingId)
            throws UserNotFoundException, BookingNotFoundException, ItemNotBelongsToUserException {
        log.info("Получен запрос - получение информации о бронировании по переданному id: " + bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsForRequester(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam Integer from, @RequestParam Integer size)
            throws UserNotFoundException, UnsupportedStatusException {
        try {
            State stateToConvert = State.valueOf(state);
            log.info("Получен запрос - получение информации по всем бронированиям для владельца id: " + userId);
            return bookingService.getAllBookingsForRequesterWithPagination(userId, stateToConvert, from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam Integer from, @RequestParam Integer size)
            throws UserNotFoundException, UnsupportedStatusException {
        try {
            State stateToConvert = State.valueOf(state);
            log.info("Получен запрос - получение информации по всем бронированиям для владельца id: " + userId);
            return bookingService.getAllBookingsForOwnerWithPagination(userId, stateToConvert, from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }
}