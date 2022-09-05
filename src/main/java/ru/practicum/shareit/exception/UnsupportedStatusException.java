package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends RuntimeException {

    public UnsupportedStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}