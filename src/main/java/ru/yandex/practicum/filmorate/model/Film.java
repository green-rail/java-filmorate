package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Data
@Builder
public class Film {
    @Min(value = 0)
    @With
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть null")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть больше нуля")
    private int duration;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final HashSet<Long> likes = new HashSet<>();

    public boolean addLike(Long userId) {
        return likes.add(userId);
    }

    public boolean removeLike(Long userId) {
        return likes.remove(userId);
    }

    public int getLikesCount() {
        return likes.size();
    }

    public Collection<Long> getLikes() {
        return Collections.unmodifiableCollection(likes);
    }
}
