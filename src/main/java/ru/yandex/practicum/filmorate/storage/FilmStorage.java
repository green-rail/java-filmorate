package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    boolean indexExists(long id);

    Optional<Film> getFilm(long id);

    long put(Film film);

    void updateFilm(Film film);

    Collection<Film> getAll();

    Collection<Genre> getAllGenres();

    Optional<Genre> getGenre(int id);

    Optional<Mpa> getMpa(int id);

    Collection<Mpa> getAllMpas();

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
