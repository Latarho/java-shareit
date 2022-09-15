package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestCreatingDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    // Создание запроса на вещь
    ItemRequestDto create(Long userId, ItemRequestCreatingDto itemRequestCreatingDto) throws UserNotFoundException;
    // Получение информации по всем запросам на вещь для пользователя
    List<ItemRequestWithItemsDto> getByRequesterId(Long userId)
            throws UserNotFoundException, ItemRequestNotFoundException;
    // Поиск запроса на вещь по Id
    ItemRequestWithItemsDto getById(Long requestId, Long userId)
            throws UserNotFoundException, ItemRequestNotFoundException;
    // Получение информации по всем запросам на вещь для пользователя
    List<ItemRequestWithItemsDto> getAllWithPagination(Long userId, Integer from, Integer size)
            throws UserNotFoundException, ItemRequestNotFoundException;
}