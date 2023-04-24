package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    @Test
    void addFilm() {
        var service = new FilmService(new InMemoryFilmStorage());
        var film1 = service.addFilm(makeFilmWithName("Film 1"));
        assertTrue(service.filmExists(film1.getId()), "");
        assertFalse(service.filmExists(1000), "");
    }

    @Test
    void addAndRemoveLikes() {
        var service = new FilmService(new InMemoryFilmStorage());
        var film1 = service.addFilm(makeFilmWithName("film5Likes"));
        service.addLike(film1.getId(), 100L);
        service.addLike(film1.getId(), 101L);
        assertEquals(2, film1.getLikesCount(), "Лайки не добавились.");
        assertIterableEquals(List.of(100L, 101L), film1.getLikes(), "Лайки не добавились.");
        service.removeLike(film1.getId(), 101L);
        assertEquals(1, film1.getLikesCount(), "Лайк не удалился.");
        assertIterableEquals(List.of(100L), film1.getLikes(), "Лайк не удалился.");
    }

    @Test
    void getMostLiked() throws ValidationException {
        var service = new FilmService(new InMemoryFilmStorage());
        var film1 = service.addFilm(makeFilmWithName("film5Likes"));
        var film2 = service.addFilm(makeFilmWithName("film10Likes"));
        var film3 = service.addFilm(makeFilmWithName("film3Likes"));
        var film4 = service.addFilm(makeFilmWithName("film900Likes"));
        var film5 = service.addFilm(makeFilmWithName("film4Likes"));
        addLikes(service, film1, 5L);
        addLikes(service, film2, 10L);
        addLikes(service, film3, 3L);
        addLikes(service, film4, 900L);
        addLikes(service, film5, 4L);
        assertIterableEquals(List.of(film4, film2, film1), service.getMostLiked(3));
    }


    @Test
    void updateFilm() {
        var service = new FilmService(new InMemoryFilmStorage());
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
        for (long i = 0; i < likesCount; i++) {
            service.addLike(film.getId(), i);
        }
    }

    private static Film makeFilmWithName(String name) {
        return Film.builder()
                .name(name)
                .description("Description")
                .releaseDate(LocalDate.of(2007, 5, 3))
                .duration(120)
                .build();
    }
}