package ru.yandex.practicum.filmorate.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Ресурс не найден";

    public ResourceNotFoundException() {
        super(defaultMessage);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
