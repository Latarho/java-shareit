package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Маппинг объекта класса Item в ItemDto и наоборот.
 */
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null);
    }

    public static Item toItem(ItemCreatingDto itemCreatingDto) {
        return new Item(
                null,
                itemCreatingDto.getName(),
                itemCreatingDto.getDescription(),
                itemCreatingDto.getAvailable(),
                null,
                itemCreatingDto.getRequestId() != null ? itemCreatingDto.getRequestId() : null);
    }

    public static ItemWithCommentDto toItemDtoWithComment(Item item, List<CommentForItemDto> comments,
                                                          BookingItemDto lastBooking, BookingItemDto nextBooking) {
        return new ItemWithCommentDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                comments,
                lastBooking,
                nextBooking
        );
    }
}