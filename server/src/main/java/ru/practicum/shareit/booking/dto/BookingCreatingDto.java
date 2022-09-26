package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Dto - создание бронирования.
 */
@Data
@AllArgsConstructor
public class BookingCreatingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}