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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        checkUser(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwnerId(ownerId);
        return ItemMapper.toItemDto(itemStorage.create(newItem));
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemStorage.getById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(Long ownerId, ItemDto itemDto, Long itemId) {
        checkUser(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwnerId(ownerId);
        Item updateItem = itemStorage.getById(itemId);
        if (newItem.getOwnerId() != updateItem.getOwnerId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "У пользователя: " + ownerId + " нет вещи: " + itemId);
        }
        if (newItem.getName() != null) {
            updateItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            updateItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            updateItem.setAvailable(newItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.update(updateItem));
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        return itemStorage.searchItem(request).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkUser(Long id) {
        if (userStorage.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует пользователь id: " + id);
        }
    }
}