package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public boolean indexExists(long id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.getOrDefault(id, null));
    }

    @Override
    public void put(User user) {
        users.put(user.getId(), user);
    }
}
