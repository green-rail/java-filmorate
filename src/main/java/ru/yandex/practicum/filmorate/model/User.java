package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @Min(0)
    @With
    private final int    id;
    @Email(message = "Некорректный email")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    private final String login;
    private String    name;
    private LocalDate birthday;
}
