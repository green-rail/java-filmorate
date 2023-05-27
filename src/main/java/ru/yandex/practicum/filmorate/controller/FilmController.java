package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InvalidParamException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") Long id) {
        if (!filmService.filmExists(id)) {
            throw new FilmNotFoundException();
        }
        return filmService.getFilm(id)
                .orElseThrow(FilmNotFoundException::new);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film setLike(@PathVariable("id") Long id,
                        @PathVariable("userId") Long userId) {

        if (!filmService.filmExists(id)) {
            throw new FilmNotFoundException();
        }
        if (!userService.userExists(userId)) {
            throw new UserNotFoundException();
        }
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        if (!filmService.filmExists(id)) {
            throw new FilmNotFoundException();
        }
        if (!userService.userExists(userId)) {
            throw new UserNotFoundException();
        }
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new InvalidParamException("count");
        }
        return filmService.getMostLiked(count);
    }

    @GetMapping("/genres")
    public Collection<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        return filmService.getGenre(id);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getRatings() {
        return filmService.getMpas();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable("id") Integer id) {
        return filmService.getMpa(id);
    }
}
