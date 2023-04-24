package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Пользователь не найден";

    public UserNotFoundException() {
        super(defaultMessage);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
