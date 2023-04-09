package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Min(value = 0, message = "Неверный идентификатор")
    @With
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotBlank(message = "Название не должно быть пустым.")
    private String    name;

    @NotNull(message = "Описание не может быть null")
    private String    description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(1)
    private int       duration;
}
