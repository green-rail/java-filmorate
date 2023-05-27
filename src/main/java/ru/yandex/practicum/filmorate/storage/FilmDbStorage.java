package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcFilmInsert;

    private final Map<Integer, Mpa> mpaCache = new HashMap<>();

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcFilmInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("PUBLIC.FILMS")
                .usingColumns("FILM_NAME", "DESCRIPTION", "RELEASE_DATE", "DURATION", "MPA_ID")
                .usingGeneratedKeyColumns("FILM_ID");

        loadMpas().forEach(mpa -> mpaCache.put(mpa.getId(), mpa));
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
    public long put(Film film) {

        long filmIndex = jdbcFilmInsert.executeAndReturnKey(Map.of(
                "FILM_NAME",    film.getName(),
                "DESCRIPTION",  film.getDescription(),
                "RELEASE_DATE", film.getReleaseDate(),
                "DURATION",     film.getDuration(),
                "MPA_ID",    film.getMpa().getId()
        )).longValue();

        if (film.getLikesCount() > 0) {
            String sql = "INSERT INTO PUBLIC.LIKES (FILM_ID, USER_ID) values (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Long id : film.getLikes()) {
                Object[] args = {filmIndex, id};
                batchArgs.add(args);
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }

        if (film.getGenres() != null && film.getGenres().size() > 0) {
            String sql = "INSERT INTO PUBLIC.FILMS_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre g : film.getGenres()) {
                Object[] args = {filmIndex, g.getId()};
                batchArgs.add(args);
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
        return filmIndex;
    }

    @Override
    public void updateFilm(Film film) {
        String sql = "UPDATE PUBLIC.FILMS SET "
                + "film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        sql = "DELETE FROM PUBLIC.FILMS_GENRES where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            sql = "INSERT INTO PUBLIC.FILMS_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre g : film.getGenres()) {
                Object[] args = {film.getId(), g.getId()};
                batchArgs.add(args);
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        String sql = "select * from PUBLIC.GENRES where genre_id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id);
            return Optional.ofNullable(genre);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "select * from PUBLIC.GENRES";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }

    @Override
    public Optional<Mpa> getMpa(int id) {
        return Optional.ofNullable(mpaCache.get(id));
    }

    private Collection<Mpa> loadMpas() {
        String sql = "select * from PUBLIC.MPA";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    @Override
    public Collection<Mpa> getAllMpas() {
        return mpaCache.values();
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO PUBLIC.LIKES(film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM PUBLIC.LIKES where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private class FilmRowMapper implements RowMapper<Film> {
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("film_id");
            String name = rs.getString("film_name");
            String description = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            int duration = rs.getInt("duration");
            int mpaId = rs.getInt("mpa_id");
            var origin = mpaCache.get(mpaId);
            var mpa = new Mpa();
            mpa.setId(origin.getId());
            mpa.setName(origin.getName());
            Film film =  Film.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .releaseDate(releaseDate)
                    .duration(duration)
                    .mpa(mpa)
                    .build();
            String sql = "select user_id from PUBLIC.LIKES where film_id = ?";
            jdbcTemplate.queryForList(sql, Long.class, film.getId())
                    .forEach(film::addLike);

            sql = "SELECT g.genre_id, g.genre_name "
                  + "FROM films f "
                  + "JOIN films_genres fg ON f.film_id = fg.film_id "
                  + "JOIN genres g ON fg.genre_id = g.genre_id "
                  + "WHERE f.film_id = ?;";

            film.setGenres(new ArrayList<>());
            jdbcTemplate.query(sql, new GenreRowMapper(), id).forEach(film::addGenre);
            return film;
        }
    }

    private static class GenreRowMapper implements  RowMapper<Genre> {
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("genre_id");
            String name = rs.getString("genre_name");
            var g = new Genre();
            g.setId(id);
            g.setName(name);
            return g;
        }
    }

    private static class MpaRowMapper implements  RowMapper<Mpa> {
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("mpa_id");
            String name = rs.getString("mpa_name");
            var mpa = new Mpa();
            mpa.setId(id);
            mpa.setName(name);
            return mpa;
        }
    }
}
