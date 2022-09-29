package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.HeaderKey;
import ru.practicum.shareit.item.dto.CommentCreatingDto;
import ru.practicum.shareit.item.dto.ItemCreatingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @Valid @RequestBody ItemCreatingDto itemCreatingDto) {
        log.info("Получен запрос - создание новой вещи: " + itemCreatingDto.toString());
        return itemClient.create(userId, itemCreatingDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderKey.USER_KEY) Long userId, @PathVariable Long id) {
        log.info("Получен запрос - получение вещи по переданному id: " + id);
        return itemClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @RequestParam(required = false, defaultValue = "0")
                                         @Min(0) Integer from,
                                         @RequestParam(required = false, defaultValue = "10")
                                         @Min(1) Integer size) {
        log.info("Получен запрос - получение списка вещей");
        return itemClient.getAll(userId, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                         @PathVariable Long id, @RequestBody ItemCreatingDto itemCreatingDto) {
        log.info("Получен запрос - обновление существующей вещи: " + itemCreatingDto.toString());
        return itemClient.update(userId, id, itemCreatingDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                      @RequestParam String text,
                                      @RequestParam(required = false, defaultValue = "0")
                                      @Min(0) Integer from,
                                      @RequestParam(required = false, defaultValue = "10")
                                      @Min(1) Integer size) {
        if (text.isEmpty()) {
            return ResponseEntity.ok().body(List.of());
        } else {
            log.info("Получен запрос - поиск вещи по тексту: " + text);
            return itemClient.searchByText(userId, text, from, size);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HeaderKey.USER_KEY) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CommentCreatingDto commentCreatingDto) {
        log.info("Получен запрос - добавление отзыва к вещи id: " + itemId);
        return itemClient.createComment(userId, itemId, commentCreatingDto);
    }
}