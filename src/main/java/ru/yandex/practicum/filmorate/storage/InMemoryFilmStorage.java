package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    private long filmId = 0;

    @Override
    public boolean indexExists(long id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.getOrDefault(id, null));
    }

    public long put(Film film) {
        var newFilm = film.withId(filmId++);
        films.put(newFilm.getId(), newFilm);
        return newFilm.getId();
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void removeLike(long filmId, long userId) {

    }
}
