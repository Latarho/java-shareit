package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.helpers.HeaderKey;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                          @Valid @RequestBody ItemCreatingDto itemCreatingDto)
            throws UserNotFoundException, ValidationException {
        log.info("Получен запрос - создание новой вещи: " + itemCreatingDto.toString());
        return itemService.create(userId, itemCreatingDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                          @PathVariable Long id, @RequestBody ItemCreatingDto itemCreatingDto)
            throws ValidationException, AuthFailedException {
        log.info("Получен запрос - обновление существующей вещи: " + itemCreatingDto.toString());
        return itemService.update(userId, id, itemCreatingDto);
    }

    @GetMapping("/{id}")
    public ItemWithCommentDto getById(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long id)
            throws ItemNotFoundException, ValidationException, UserNotFoundException {
        log.info("Получен запрос - получение вещи по переданному id: " + id);
        return itemService.getById(userId, id);
    }

    @GetMapping
    public List<ItemWithCommentDto> getAll(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                           @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size)
            throws UserNotFoundException {
        log.info("Получен запрос - получение списка вещей");
        return itemService.getAllWithPagination(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestHeader(HeaderKey.USER_KEY) Long userId, @RequestParam String text,
                                      @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                      @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size)
            throws UserNotFoundException {
        log.info("Получен запрос - поиск вещи по тексту: " + text);
        return itemService.searchByTextWithPagination(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long itemId,
                                    @RequestBody @Valid CommentCreatingDto commentCreatingDto)
            throws UserNotFoundException, ValidationException {
        log.info("Получен запрос - добавление отзыва к вещи id: " + itemId);
        return itemService.createComment(userId, itemId, commentCreatingDto);
    }
}