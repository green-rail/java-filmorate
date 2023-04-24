package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Фильм не найден";

    public FilmNotFoundException() {
        super(defaultMessage);
    }

    public FilmNotFoundException(String message) {
        super(message);
    }
}
