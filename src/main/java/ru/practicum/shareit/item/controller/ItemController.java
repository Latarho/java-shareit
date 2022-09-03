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
            throws NoHeaderException, UserNotFoundException, ValidationException {
        log.info("Получен запрос - создание новой вещи: " + itemCreatingDto.toString());
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.create(userId, itemCreatingDto);
        }
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                          @PathVariable Long id, @RequestBody ItemCreatingDto itemCreatingDto)
            throws NoHeaderException, ValidationException, AuthFailedException {
        log.info("Получен запрос - обновление существующей вещи: " + itemCreatingDto.toString());
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.update(userId, id, itemCreatingDto);
        }
    }

    @GetMapping("/{id}")
    public ItemWithCommentDto getById(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long id)
            throws NoHeaderException, ItemNotFoundException, ValidationException, UserNotFoundException {
        log.info("Получен запрос - получение вещи по переданному id: " + id);
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.getById(userId, id);
        }
    }

    @GetMapping
    public List<ItemWithCommentDto> getAll(@RequestHeader(HeaderKey.USER_KEY) Long userId)
            throws NoHeaderException, UserNotFoundException {
        log.info("Получен запрос - получение списка вещей");
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.getAll(userId);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestHeader(HeaderKey.USER_KEY) Long userId, @RequestParam String text)
            throws NoHeaderException, UserNotFoundException {
        log.info("Получен запрос - поиск вещи по тексту: " + text);
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.searchByText(userId, text);
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long itemId,
                                    @RequestBody @Valid CommentCreatingDto commentCreatingDto)
            throws NoHeaderException, UserNotFoundException, ValidationException {
        log.info("Получен запрос - добавление отзыва к вещи id: " + itemId);
        if (userId == null) {
            throw new NoHeaderException("В запросе отсутствует заголовок");
        } else {
            return itemService.createComment(userId, itemId, commentCreatingDto);
        }
    }
}