package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item create(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        List<Item> itemsWithOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId() == userId) {
                itemsWithOwner.add(item);
            }
        }
        return itemsWithOwner;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchItem(String request) {
        List<Item> searchItems = new ArrayList<>();
        if (request.isEmpty() || request.isBlank()) {
            return searchItems;
        }
        request = request.toLowerCase();
        for (Item item : items.values()) {
            if ((item.getAvailable().equals(true) && item.getName().toLowerCase().contains(request)) ||
                    (item.getAvailable().equals(true) && item.getDescription().toLowerCase().contains(request))) {
                searchItems.add(item);
            }
        }
        return searchItems;
    }

    private long generateItemId() {
        id++;
        return id;
    }
}