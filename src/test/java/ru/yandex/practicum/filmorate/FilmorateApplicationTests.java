package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Optional;



@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;


    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUser(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void getAllUsers() {
        var users = userStorage.getUsers();

        assertEquals(5, users.size());

    }

    @Test
    public void putUser() {

        var u = User.builder()
                .email("test@email.ru")
                .login("test_login")
                .name("Testname")
                .birthday(LocalDate.of(1990, 3, 2))
                .build();

        userStorage.put(u);

        Optional<User> userOptional = userStorage.getUser(6);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                        assertThat(user).hasFieldOrPropertyWithValue("id", 6L);
                        assertThat(user).hasFieldOrPropertyWithValue("email", "test@email.ru");
                    }
                );
    }

    @Test
    public void findFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilm(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );

    }


    @Test
    public void getAllFilms() {

        var films = filmStorage.getAll();

        assertEquals(5, films.size());
    }

    @Test
    public void putFilm() {

        var movie = Film.builder()
                .name("Test Movie")
                .description("Test description")
                .releaseDate(LocalDate.of(2012, 3, 10))
                .duration(115)
                .ratingId(3)
                .build();

        filmStorage.put(movie);

        Optional<Film> filmOptional = filmStorage.getFilm(6L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", 6L);
                            assertThat(film).hasFieldOrPropertyWithValue("name", "Test Movie");
                            assertThat(film).hasFieldOrPropertyWithValue("ratingId", 3);
                        }
                );
    }


}
