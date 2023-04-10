package ru.yandex.practicum.filmorate.model;

//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Min(value = 0, message = "Неверный идентификатор")
    @With
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotEmpty(message = "Название не должно быть пустым.")
    private String    name;

    @NotNull(message = "Описание не может быть null")
    private String    description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(1)
    private int       duration;
}
