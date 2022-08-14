package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long ownerId, ItemDto itemDto);

    ItemDto getById(long id);

    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto update(long ownerId, ItemDto itemDto, long itemId);

    List<ItemDto> searchItem(String request);
}