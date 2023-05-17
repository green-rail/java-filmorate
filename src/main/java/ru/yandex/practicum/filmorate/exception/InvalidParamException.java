package ru.yandex.practicum.filmorate.exception;

public class InvalidParamException extends RuntimeException {
    private final String parameter;

    public InvalidParamException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
