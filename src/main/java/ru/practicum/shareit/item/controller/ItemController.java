package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id"; //идентификатор пользователя, который добавляет вещь (владелец вещи)
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@NotEmpty @RequestHeader(USER_ID_HEADER) long ownerId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос - создание вещи id: " + itemDto.toString());
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NotEmpty @RequestHeader(USER_ID_HEADER) long ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("Получен запрос - обновление существующей вещи id: " + itemId);
        return itemService.update(ownerId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@NotEmpty @RequestHeader(USER_ID_HEADER) long id,
                               @PathVariable long itemId) {
        log.info("Получен запрос - получение вещи по переданному id: " + itemId);
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@NotEmpty @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос - получение списка вещей закрепленных за пользователем id: " + userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Получен запрос - поиск вещи по тексту: " + text);
        return itemService.searchItem(text);
    }
}