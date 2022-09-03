package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

/**
 * Dto - вещь с отзывом и бронированием.
 */
@Data
@AllArgsConstructor
public class ItemWithCommentDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentForItemDto> comments;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}