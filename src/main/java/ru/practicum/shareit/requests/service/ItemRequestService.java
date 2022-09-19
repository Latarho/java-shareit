package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    // Создание запроса на вещь
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);
    // Получение информации по всем запросам на вещь для пользователя
    List<ItemRequestDto> getByRequesterId(Long userId);
    // Поиск запроса на вещь по Id
    List<ItemRequestDto> getById(Long userId, Integer from, Integer size);
    // Получение информации по всем запросам на вещь для пользователя
    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) throws ItemRequestNotFoundException;
}