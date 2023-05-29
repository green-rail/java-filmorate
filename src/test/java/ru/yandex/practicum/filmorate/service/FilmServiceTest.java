package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryMpaStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    FilmService filmService;

    @BeforeEach
    void makeFilmServiceWithUsers() {

        var userStorage = new InMemoryUserStorage();
        addUsers(userStorage, 20);

        filmService = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryMpaStorage(),
                new InMemoryGenreStorage(),
                userStorage);
    }





    @Test
    void addFilm() {
        var service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryMpaStorage(),
                new InMemoryGenreStorage(),
                new InMemoryUserStorage());
        var film1 = service.addFilm(makeFilmWithName("Film 1"));
        assertTrue(service.filmExists(film1.getId()), "");
        assertFalse(service.filmExists(1000), "");
    }

    @Test
    void addAndRemoveLikes() {
        var film1 = filmService.addFilm(makeFilmWithName("film5Likes"));
        filmService.addLike(film1.getId(), 10L);
        filmService.addLike(film1.getId(), 11L);
        assertEquals(2, film1.getLikesCount(), "Лайки не добавились.");
        assertIterableEquals(List.of(10L, 11L), film1.getLikes(), "Лайки не добавились.");
        filmService.removeLike(film1.getId(), 11L);
        assertEquals(1, film1.getLikesCount(), "Лайк не удалился.");
        assertIterableEquals(List.of(10L), film1.getLikes(), "Лайк не удалился.");
    }

    @Test
    void getMostLiked() throws ValidationException {
        var film1 = filmService.addFilm(makeFilmWithName("film5Likes"));
        var film2 = filmService.addFilm(makeFilmWithName("film10Likes"));
        var film3 = filmService.addFilm(makeFilmWithName("film3Likes"));
        var film4 = filmService.addFilm(makeFilmWithName("film900Likes"));
        var film5 = filmService.addFilm(makeFilmWithName("film4Likes"));
        addLikes(filmService, film1, 5L);
        addLikes(filmService, film2, 10L);
        addLikes(filmService, film3, 3L);
        addLikes(filmService, film4, 12L);
        addLikes(filmService, film5, 4L);
        assertIterableEquals(List.of(film4.getId(), film2.getId(), film1.getId()),
                filmService.getMostLiked(3).stream().map(Film::getId).collect(Collectors.toList()));
    }

    private void addUsers(InMemoryUserStorage storage, int count) {
        for (int i = 1; i <= count; i++) {
            var user = User.builder()
                    .id(i)
                    .login("a" + i)
                    .email("email" + i + "@mail.ru")
                    .build();
            storage.put(user);
        }
    }


    @Test
    void updateFilm() {
        //var service = new FilmService(new InMemoryFilmStorage(), new InMemoryMpaStorage(), new InMemoryGenreStorage());
        var service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryMpaStorage(),
                new InMemoryGenreStorage(),
                new InMemoryUserStorage());
        var film1 = service.addFilm(makeFilmWithName("Film 1"));
        assertEquals(film1, service.getFilms().stream().findFirst().get(), "");
        var film2 = film1.withId(film1.getId());
        film2.setName("Updated film name");
        service.updateFilm(film2);
        assertEquals(
                "Updated film name",
                service.getFilms().stream().findFirst().get().getName(),
                "");
    }

    private static void addLikes(FilmService service, Film film, long likesCount) {
        for (long i = 1; i < likesCount; i++) {
            service.addLike(film.getId(), i);
        }
    }

    private static Film makeFilmWithName(String name) {
        return Film.builder()
                .name(name)
                .description("Description")
                .releaseDate(LocalDate.of(2007, 5, 3))
                .duration(120)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
    }
}