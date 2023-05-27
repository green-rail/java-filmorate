package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage storage;
    private static final LocalDateTime earliestThreshold = LocalDateTime.of(
            1895, Month.DECEMBER, 28, 0, 0, 0);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage storage) {
        this.storage = storage;
    }

    public Film addFilm(Film film) throws ValidationException {
        var validationMessage = validateFilm(film);
        if (validationMessage.isPresent()) {
            throw new ValidationException("Некорректный фильм: " + validationMessage.get());
        }
        stripDuplicates(film);
        long id = storage.put(film);
        Film newFilm = film.withId(id);
        log.info("Фильм добавлен: {}", newFilm);
        return newFilm;
    }

    public Film addLike(Long id, Long userId) throws ValidationException {
        var optFilm = storage.getFilm(id);
        if (optFilm.isPresent()) {
            optFilm.get().addLike(userId);
            storage.addLike(id, userId);
            return optFilm.get();
        }
        throw new FilmNotFoundException();
    }

    public Collection<Film> getFilms() {
        return storage.getAll();
    }

    public Optional<Film> getFilm(Long id) {
        return storage.getFilm(id);
    }

    public Film removeLike(long id, long userId) {
        var optFilm = storage.getFilm(id);
        if (optFilm.isPresent()) {
            optFilm.get().removeLike(userId);
            storage.removeLike(id, userId);
            return optFilm.get();
        }
        throw new FilmNotFoundException();
    }

    public boolean filmExists(long id) {
        return storage.indexExists(id);
    }

    public Collection<Film> getMostLiked(int count) {
        return storage.getAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toUnmodifiableList());
    }

    public Collection<Genre> getGenres() {
        return  storage.getAllGenres();
    }

    public Genre getGenre(Integer id) {
        return storage.getGenre(id).orElseThrow(FilmNotFoundException::new);
    }

    public Mpa getMpa(Integer id) {
        return storage.getMpa(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Collection<Mpa> getMpas() {
        return storage.getAllMpas();
    }

    public Film updateFilm(Film film) throws ValidationException, ResponseStatusException {
        var validationMessage = validateFilm(film);
        if (validationMessage.isPresent()) {
            throw new ValidationException("Некорректный фильм: " + validationMessage.get());
        }
        if (storage.indexExists(film.getId())) {
            stripDuplicates(film);
            storage.updateFilm(film);
            log.info("Фильм обновлен: {}", film);
        } else {
            log.warn("Фильм не найден: {}", film);
            throw new FilmNotFoundException();
        }
        return film;
    }

    public static Optional<String> validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            return Optional.of("описание должно быть меньше 200 знаков.");
        }
        if (film.getDuration() <= 0) {
            return Optional.of("продолжительность должна быть больше 0.");
        }
        if (film.getMpa() == null) {
            return Optional.of("отсутствует рейтинг mpa.");
        }
        if (earliestThreshold.isAfter(film.getReleaseDate().atStartOfDay())) {
            return Optional.of("неверная дата выхода.");
        }
        return Optional.empty();
    }

    private static void stripDuplicates(Film film) {
        if (film.getGenres() != null && film.getGenres().size() > 1) {
            film.setGenres(film.getGenres().stream()
                    .sorted(Comparator.comparingInt(Genre::getId))
                    .distinct()
                    .collect(Collectors.toList())
            );
        }
    }
}
