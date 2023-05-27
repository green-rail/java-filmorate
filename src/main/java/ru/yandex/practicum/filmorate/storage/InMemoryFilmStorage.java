package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public boolean indexExists(long id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.getOrDefault(id, null));
    }

    public long put(Film film) {
        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {
        put(film);
    }

    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Mpa> getMpa(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Mpa> getAllMpas() {
        return null;
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void removeLike(long filmId, long userId) {

    }
}
