package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.helpers.HeaderKey;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
										 @RequestBody @Valid BookingCreatingDto bookingCreatingDto) {
		log.info("Получен запрос - создание нового бронирования: " + bookingCreatingDto.toString());
		return bookingClient.create(userId, bookingCreatingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@RequestHeader(HeaderKey.USER_KEY) Long userId,
										 @PathVariable Long bookingId,
										 @RequestParam Boolean approved) {
		log.info("Получен запрос - обновление информации о бронировании (статус) id: " + bookingId.toString());
		return bookingClient.update(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader(HeaderKey.USER_KEY) Long userId,
										  @PathVariable Long bookingId) {
		log.info("Получен запрос - получение информации о бронировании по переданному id: " + bookingId);
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping()
	public ResponseEntity<Object> getAllBookingsForRequester(@RequestHeader(HeaderKey.USER_KEY) Long userId,
															 @RequestParam(defaultValue = "ALL") String state,
															 @RequestParam(required = false, defaultValue = "0")
													   		 @Min(0) Integer from,
															 @RequestParam(required = false, defaultValue = "10")
													   		 @Min(1) Integer size) throws UnsupportedStatusException {
		try {
			State stateToConvert = State.valueOf(state);
			log.info("Получен запрос - получение информации по всем бронированиям для пользователя id: " + userId);
			return bookingClient.getAllBookingsForRequester(userId, stateToConvert, from, size);
		} catch (IllegalArgumentException e) {
			throw new UnsupportedStatusException("Unknown state: " + state);
		}
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsForOwner(@RequestHeader(HeaderKey.USER_KEY) Long userId,
														 @RequestParam(defaultValue = "ALL") String state,
														 @RequestParam(required = false, defaultValue = "0")
														 @Min(0) Integer from,
														 @RequestParam(required = false, defaultValue = "10")
														 @Min(1) Integer size) throws UnsupportedStatusException {
		try {
			State stateToConvert = State.valueOf(state);
			log.info("Получен запрос - получение информации по всем бронированиям для владельца id: " + userId);
			return bookingClient.getAllBookingsForOwner(userId, stateToConvert, from, size);
		} catch (IllegalArgumentException e) {
			throw new UnsupportedStatusException("Unknown state: " + state);
		}
	}
}