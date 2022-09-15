package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequestForCreate(ItemRequestCreatingDto itemRequestCreatingDto) {
        return new ItemRequest(
                null,
                itemRequestCreatingDto.getDescription(),
                null,
                null
        );
    }

    public static ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestWithItemsDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}