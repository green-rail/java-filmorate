package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    boolean indexExists(long id);

    Optional<Film> getFilm(long id);

    long put(Film film);

    void updateFilm(Film film);

    Collection<Film> getAll();

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
