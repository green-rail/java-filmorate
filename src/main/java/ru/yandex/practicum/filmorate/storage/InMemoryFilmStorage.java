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

    @Override
    public boolean indexExists(long id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.getOrDefault(id, null));
    }

    public void put(Film film) {
        films.put(film.getId(), film);
    }

    public Collection<Film> getAll() {
        return films.values();
    }
}
