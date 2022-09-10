package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto - информация о бронировании в информации о вещи.
 */
@Data
@AllArgsConstructor
public class BookingItemDto {
    private Long id;
    private Long bookerId;
}