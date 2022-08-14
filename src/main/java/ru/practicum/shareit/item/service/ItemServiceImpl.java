package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        checkUser(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userStorage.getById(ownerId));
        return ItemMapper.toItemDto(itemStorage.create(newItem));
    }

    @Override
    public ItemDto getById(long id) {
        return ItemMapper.toItemDto(itemStorage.getById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        List<Item> itemsWithOwner = itemStorage.getAllItemsByUserId(userId);
        List<ItemDto> itemsDtoWithOwner = new ArrayList<>();
        for (Item item : itemsWithOwner) {
            itemsDtoWithOwner.add(ItemMapper.toItemDto(item));
        }
        return itemsDtoWithOwner;
    }

    @Override
    public ItemDto update(long ownerId, ItemDto itemDto, long itemId) {
        checkUser(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userStorage.getById(ownerId));
        Item updateItem = itemStorage.getById(itemId);
        if (newItem.getOwner() != updateItem.getOwner()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "У пользователя: " + ownerId + " нет вещи: " + itemId);
        }
        if (newItem.getName() != null) {
            updateItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            updateItem.setDescription(newItem.getDescription(

            ));
        }
        if (newItem.getAvailable() != null) {
            updateItem.setAvailable(newItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.update(updateItem));
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        List<Item> foundItems = itemStorage.searchItem(request);
        List<ItemDto> foundItemsDto = new ArrayList<>();
        for (Item item : foundItems) {
            if (item.getAvailable()) {
                foundItemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        return foundItemsDto;
    }

    private void checkUser(long id) {
        if (userStorage.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует пользователь id: " + id);
        }
    }
}