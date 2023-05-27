package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcUserInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("PUBLIC.USERS")
                .usingColumns("EMAIL", "LOGIN", "NAME", "BIRTHDAY")
                .usingGeneratedKeyColumns("USER_ID");
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "select * from PUBLIC.USERS";
        return jdbcTemplate.query(sql, new UserRawMapper());
    }

    @Override
    public boolean indexExists(long id) {
        String sql = "select count(*) from users where user_id = ?";
        Integer number = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return (number != null && number > 0);
    }

    @Override
    public Optional<User> getUser(long id) {
        String sql = "select * from users where user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRawMapper(), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(User user) {
        final long userIndex = jdbcUserInsert.executeAndReturnKey(Map.of(
                "EMAIL",    user.getEmail(),
                "LOGIN",    user.getLogin(),
                "NAME",     user.getName(),
                "BIRTHDAY", user.getBirthday()
        )).longValue();

        if (user.getFriends().size() == 0) {
            return;
        }

        String sql = "INSERT INTO PUBLIC.FRIENDS (USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                user.getFriends()
                        .stream()
                        .map(id -> new Object[]{userIndex, id})
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE PUBLIC.USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO PUBLIC.FRIENDS(user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM PUBLIC.FRIENDS where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private class UserRawMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user =  User.builder()
                    .id(rs.getInt("user_id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();

            String sql = "select friend_id from PUBLIC.FRIENDS where user_id = ? ";
            jdbcTemplate.queryForList(sql, Long.class, user.getId())
                    .forEach(user::addFriend);
            return user;
        }
    }
}
