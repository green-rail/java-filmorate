package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean indexExists(long id) {
        String sql = "select count(*) from films where film_id = ?";
        Integer number = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return (number != null && number > 0);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        String sql = "select * from PUBLIC.FILMS where film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);
            return Optional.ofNullable(film);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void put(Film film) {
        String sql = "INSERT INTO PUBLIC.FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE, RATING) " +
                "values (?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenre(),
                film.getRating().getRating());

        sql = "INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) values (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long id : film.getLikes()) {
            Object[] args = {film.getId(), id};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    private class FilmRowMapper implements RowMapper<Film> {
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("film_id");
            String name = rs.getString("film_name");
            String description = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            int duration = rs.getInt("duration");
            String genre = rs.getString("genre");
            Rating rating = Rating.fromString(rs.getString("rating").toUpperCase());
            Film film =  Film.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .releaseDate(releaseDate)
                    .duration(duration)
                    .genre(genre)
                    .rating(rating)
                    .build();
            String sql = "select user_id from likes where film_id = ?";
            jdbcTemplate.queryForList(sql, Long.class, film.getId())
                    .forEach(film::addLike);
            return film;
        }
    }
}
