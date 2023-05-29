package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, Mpa> mpaCache = new HashMap<>();

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        loadMpas().forEach(mpa -> mpaCache.put(mpa.getId(), mpa));
    }

    @Override
    public Optional<Mpa> getMpa(int id) {
        return Optional.ofNullable(mpaCache.get(id));
    }

    @Override
    public Collection<Mpa> getAllMpas() {
        return mpaCache.values();
    }

    private Collection<Mpa> loadMpas() {
        String sql = "select * from PUBLIC.MPA";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    private static class MpaRowMapper implements RowMapper<Mpa> {
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("mpa_id");
            String name = rs.getString("mpa_name");
            return Mpa.builder().id(id).name(name).build();
        }
    }
}
