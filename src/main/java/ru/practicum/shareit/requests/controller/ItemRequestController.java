package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.helpers.HeaderKey;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto)
            throws UserNotFoundException {
        log.info("Получен запрос - создание нового запроса на вещь: " + itemRequestDto.toString());
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> getByRequesterId(@RequestHeader(HeaderKey.USER_KEY) Long userId)
            throws UserNotFoundException {
        log.info("Получен запрос - получение информации по всем запросам на вещь для пользователя id: " + userId);
        return itemRequestService.getByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long requestId)
            throws UserNotFoundException, ItemRequestNotFoundException {
        log.info("Получен запрос - получение информации о запросе на вещь по переданному id: " + requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public Iterable<ItemRequestDto> getAll(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                                @RequestParam(required = false, defaultValue = "0")
                                                @Min(0) Integer from,
                                                @RequestParam(required = false, defaultValue = "10")
                                                @Min(1) Integer size)
            throws UserNotFoundException {
        log.info("Получен запрос - получение информации по всем запросам на вещь для пользователя id: " + userId);
        return itemRequestService.getById(userId, from, size);
    }
}