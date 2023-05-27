package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    boolean indexExists(long id);

    Optional<User> getUser(long id);

    void put(User user);

    void update(User user);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

}
