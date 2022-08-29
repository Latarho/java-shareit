package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item getById(Long id);

    List<Item> getAllItemsByUserId(Long userId);

    Item update(Item item);

    List<Item> searchItem(String request);
}