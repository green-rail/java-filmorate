package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;
    private static final LocalDateTime earliestThreshold =  LocalDateTime.of(
            1895, Month.DECEMBER, 28, 0, 0, 0);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!validateFilm(film)) {
            throw new ValidationException();
        }
        Film newFilm;
        if (!films.containsKey(film.getId())) {
            newFilm = film.withId(++idCounter);
            films.put(newFilm.getId(), newFilm);
            log.info("Фильм добавлен: {}", newFilm);
        } else {
            log.warn("Фильм уже существует: {}", film);
            throw new ValidationException();
        }
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!validateFilm(film)) {
            throw new ValidationException();
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм обновлен: {}", film);
        } else {
            log.warn("Фильм не найден: {}", film);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }
        return film;
    }


    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    static boolean validateFilm(Film film) {
        if (film.getDescription().length() > 200) return false;

        if (film.getDuration() <= 0) return false;

        if (earliestThreshold.isAfter(film.getReleaseDate().atStartOfDay())) {
            return false;
        }
        return true;
    }
}
