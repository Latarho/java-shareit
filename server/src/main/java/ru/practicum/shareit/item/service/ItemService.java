package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.AuthFailedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemCreatingDto itemCreatingDto) throws ValidationException, UserNotFoundException;

    ItemWithCommentDto getById(Long ownerId, Long id) throws ValidationException, ItemNotFoundException,
            UserNotFoundException;

    List<ItemWithCommentDto> getAllWithPagination(Long userId, Integer from, Integer size) throws UserNotFoundException;

    ItemDto update(Long ownerId, Long itemId, ItemCreatingDto itemCreatingDt) throws ValidationException,
            AuthFailedException;

    List<ItemDto> searchByTextWithPagination(Long userId, String searchText, Integer from, Integer size)
            throws UserNotFoundException;

    CommentDto createComment(Long userId, Long itemId, CommentCreatingDto commentCreatingDto) throws ValidationException,
            UserNotFoundException;
}