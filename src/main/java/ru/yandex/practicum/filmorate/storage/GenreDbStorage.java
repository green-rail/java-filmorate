package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "select * from PUBLIC.GENRES";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }



    private boolean indexExists(long id) {
        String sql = "select count(*) from genres where genre_id = ?";
        Integer number = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return (number != null && number > 0);
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        if (!indexExists(id)) {
            return Optional.empty();
        }
        String sql = "select * from PUBLIC.GENRES where genre_id = ?";
        Genre genre = jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id);
        return Optional.ofNullable(genre);
    }

    @Override
    public Collection<Genre> getGenresForFilm(long filmId) {
        String sql = "SELECT g.genre_id, g.genre_name "
                + "FROM films f "
                + "JOIN films_genres fg ON f.film_id = fg.film_id "
                + "JOIN genres g ON fg.genre_id = g.genre_id "
                + "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sql, new GenreRowMapper(), filmId);
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("genre_id");
            String name = rs.getString("genre_name");
            var g = new Genre();
            g.setId(id);
            g.setName(name);
            return g;
        }
    }
}
