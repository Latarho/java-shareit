package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(itemRequest.getItems().stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }
        return itemRequestDto;
    }

    public static List<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            listItemRequestDto.add(toItemRequestDto(itemRequest));
        }
        return listItemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}