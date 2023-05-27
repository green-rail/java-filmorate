package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;



@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;

    @Test
    @DirtiesContext
    public void getAllUsers() {
        assertEquals(0, userStorage.getUsers().size());
        addTestUser();
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    @DirtiesContext
    public void testFindUserById() {
        addTestUser();
        Optional<User> userOptional = userStorage.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );

        Optional<User> userOptionalEmpty = userStorage.getUser(999);
        assertThat(userOptionalEmpty).isNotPresent();
    }


    @Test
    @DirtiesContext
    public void putUser() {
        addTestUser();
        var u = User.builder()
                .email("test@email.ru")
                .login("test_user2")
                .name("Testname")
                .birthday(LocalDate.of(1990, 3, 2))
                .build();

        userStorage.put(u);
        Optional<User> userOptional = userStorage.getUser(2L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L);
                        assertThat(user).hasFieldOrPropertyWithValue("email", "test@email.ru");
                        assertThat(user).hasFieldOrPropertyWithValue("login", "test_user2");
                    }
                );
    }

    @Test
    @DirtiesContext
    public void updateUser() {
        addTestUser();
        var u = User.builder()
                .id(1L)
                .email("update@email.ru")
                .login("updated_login")
                .name("Updated")
                .birthday(LocalDate.of(1990, 3, 2))
                .build();
        userStorage.update(u);
        Optional<User> userOptional = userStorage.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Updated")
                );
    }

    @Test
    @DirtiesContext
    public void testFriends() {
        addTestUser();
        var u = User.builder()
                .email("friend@email.ru")
                .login("friend_login")
                .name("Friend")
                .birthday(LocalDate.of(1990, 3, 2))
                .build();
        userStorage.put(u);
        userStorage.addFriend(1L, 2L);

        Optional<User> userOptional = userStorage.getUser(1L);
        assertEquals(2L, userOptional.get().getFriends().stream().findFirst().get());
        userStorage.removeFriend(1L, 2L);
        userOptional = userStorage.getUser(1L);
        assertEquals(0, userOptional.get().getFriends().size());
    }

    @Test
    @DirtiesContext
    public void getAllFilms() {

        assertEquals(0, filmStorage.getAll().size());

        filmStorage.put(makeTestFilm());

        assertEquals(1, filmStorage.getAll().size());
    }

    @Test
    @DirtiesContext
    public void findFilmById() {
        addTestFilm();
        Optional<Film> filmOptional = filmStorage.getFilm(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );

    }

    @Test
    @DirtiesContext
    public void putFilm() {
        addTestFilm();
        var film =  Film.builder()
                .name("Test Movie 2")
                .description("Test description 2")
                .releaseDate(LocalDate.of(2012, 3, 10))
                .duration(115)
                .mpa(Mpa.builder().id(1).build())
                .build();

        filmStorage.put(film);

        Optional<Film> filmOptional = filmStorage.getFilm(2L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 2L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "Test Movie 2");
                        }
                );
    }

    @Test
    @DirtiesContext
    public void indexExists() {
        addTestFilm();
        assertTrue(filmStorage.indexExists(1L));
        assertFalse(filmStorage.indexExists(100L));
    }

    @Test
    @DirtiesContext
    public void updateFilm() {
        addTestFilm();
        var updatedFilm = Film.builder()
                .id(1L)
                .name("Updated name")
                .description("Updated description")
                .releaseDate(LocalDate.of(2012, 3, 10))
                .duration(115)
                .mpa(Mpa.builder().id(1).build())
                .build();

        filmStorage.updateFilm(updatedFilm);

        Optional<Film> filmOptional = filmStorage.getFilm(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "Updated name");
                            assertThat(f).hasFieldOrPropertyWithValue("description", "Updated description");
                        }
                );
    }

    @Test
    public void testGenres() {
        Optional<Genre> genreOpt = filmStorage.getGenre(1);
        assertThat(genreOpt)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "Комедия");
                        }
                );

        Optional<Genre> genreOptEmpty = filmStorage.getGenre(999);
        assertThat(genreOptEmpty).isNotPresent();

        assertEquals(6, filmStorage.getAllGenres().size());
    }

    @Test
    public void testMpa() {
        Optional<Mpa> mpaOpt = filmStorage.getMpa(1);
        assertThat(mpaOpt)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "G");
                        }
                );

        Optional<Mpa> mpaOptEmpty = filmStorage.getMpa(999);
        assertThat(mpaOptEmpty).isNotPresent();

        assertEquals(5, filmStorage.getAllMpas().size());
    }

    @Test
    @DirtiesContext
    public void testLikes() {
        addTestUser();
        addTestFilm();
        filmStorage.addLike(1, 1);
        var filmOpt = filmStorage.getFilm(1L);
        assertEquals(1L, filmOpt.get().getLikes().stream().findFirst().get());
        filmStorage.removeLike(1, 1);
        filmOpt = filmStorage.getFilm(1L);
        assertEquals(0, filmOpt.get().getLikes().size());
    }


    private static Film makeTestFilm() {
        return Film.builder()
                .name("Test Movie")
                .description("Test description")
                .releaseDate(LocalDate.of(2012, 3, 10))
                .duration(115)
                .mpa(Mpa.builder().id(1).build())
                .build();
    }

    private void addTestUser() {
        var u = User.builder()
                .email("test@email.ru")
                .login("test_login")
                .name("Testname")
                .birthday(LocalDate.of(1990, 3, 2))
                .build();
        userStorage.put(u);
    }

    private void addTestFilm() {
        filmStorage.put(Film.builder()
                .name("Test Movie")
                .description("Test description")
                .releaseDate(LocalDate.of(2012, 3, 10))
                .duration(115)
                .mpa(Mpa.builder().id(1).build())
                .build());
    }
}
