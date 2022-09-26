package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getByRequesterId(Long userId);

    List<ItemRequestDto> getById(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) throws ItemRequestNotFoundException;
}