package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        List<Item> itemsWithOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwnerId(), userId)) {
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

    private Long generateItemId() {
        id++;
        return id;
    }
}