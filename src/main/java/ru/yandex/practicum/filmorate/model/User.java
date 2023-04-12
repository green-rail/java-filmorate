package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @Min(0)
    @With
    private final int    id;
    @Email()
    private final String email;
    @NotBlank()
    private final String login;
    private String    name;
    private LocalDate birthday;
}
