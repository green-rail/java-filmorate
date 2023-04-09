package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Validator validator;

    @BeforeAll
    static void makeValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Film makeValidFilm() {
        return Film.builder()
                .id(0)
                .name("Inception")
                .description("The best Chris Nolan's movie.")
                .releaseDate(LocalDate.of(2007, 5, 3))
                .duration(120)
                .build();
    }

    @Test
    void validFilmShouldntThrow() {
        var film = makeValidFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Не верифицируется корректный фильм.");
        assertTrue(FilmController.validateFilm(film), "Не верифицируется корректный фильм.");
    }

    @Test
    void annotationValidationsTest() {
        var film = Film.builder().id(-1).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(4, violations.size(), "Неверное число ошибок верификации.");

        film.setName("");
        violations = validator.validate(film);
        assertEquals(4, violations.size(), "Неверное число ошибок верификации.");

        film.setName("Inception");
        film.setDescription("The best Chris Nolan's movie.");
        film.setDuration(120);
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Неверное число ошибок верификации.");
    }


    @Test
    void controllerValidationDescriptionLength() {
        var film = makeValidFilm();
        assertTrue(FilmController.validateFilm(film), "Не верифицируется корректный фильм.");
        int length = 205;
        film.setDescription("a".repeat(length));
        assertFalse(FilmController.validateFilm(film), "Неверный фильм прошел верификацию.");
    }

    @Test
    void controllerValidateDateDuration() {
        var film = makeValidFilm();
        assertTrue(FilmController.validateFilm(film), "Не верифицируется корректный фильм.");
        film.setDuration(0);
        assertFalse(FilmController.validateFilm(film), "Неверный фильм прошел верификацию.");
    }

    @Test
    void controllerValidateNotTooOld() {
        var film = makeValidFilm();
        assertTrue(FilmController.validateFilm(film), "Не верифицируется корректный фильм.");
        film.setReleaseDate(LocalDate.of(1500, 5, 3));
        assertFalse(FilmController.validateFilm(film), "Неверный фильм прошел верификацию.");
    }

}