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
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null);
    }
}