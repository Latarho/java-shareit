package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto getById(Long id);

    List<ItemDto> getAllItemsByUserId(Long userId);

    ItemDto update(Long ownerId, ItemDto itemDto, Long itemId);

    List<ItemDto> searchItem(String request);
}