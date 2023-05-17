package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    boolean indexExists(long id);

    Optional<Film> getFilm(long id);

    void put(Film film);

    Collection<Film> getAll();
}
