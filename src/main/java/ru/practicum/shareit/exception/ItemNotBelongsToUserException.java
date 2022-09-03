package ru.practicum.shareit.exception;

public class ItemNotBelongsToUserException extends Exception {

    public ItemNotBelongsToUserException(String message) {
        super(message);
    }
}