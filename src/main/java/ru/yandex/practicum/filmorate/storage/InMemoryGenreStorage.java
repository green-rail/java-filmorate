package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public class InMemoryGenreStorage implements GenreStorage {
    @Override
    public Collection<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Genre> getGenresForFilm(long filmId) {
        return null;
    }
}
