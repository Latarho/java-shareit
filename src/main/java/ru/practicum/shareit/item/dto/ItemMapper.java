package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

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
                item.getOwnerId() != null ? item.getOwnerId() : null,
                item.getRequestId() != null ? item.getRequestId() : null);
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwnerId() != null ? itemDto.getOwnerId() : null,
                itemDto.getRequestId() != null ? itemDto.getRequestId() : null);
    }
}