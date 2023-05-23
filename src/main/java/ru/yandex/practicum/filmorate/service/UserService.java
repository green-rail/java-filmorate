package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;
    private long idCounter = 0;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User addUser(User user) {
        var validationMessage = validateUser(user);
        if (validationMessage.isPresent()) {
            throw new ValidationException("Некорректный пользователь: " + validationMessage.get());
        }
        User newUser;
        newUser = user.withId(++idCounter);
        var name = user.getName() == null || user.getName().isBlank() ?
                user.getLogin() : user.getName();
        newUser.setName(name);
        storage.put(newUser);
        log.info("Пользователь добавлен: {}", newUser);
        return newUser;
    }

    public User updateUser(User user) {
        var validationMessage = validateUser(user);
        if (validationMessage.isPresent()) {
            throw new ValidationException("Некорректный пользователь: " + validationMessage.get());
        }
        if (!storage.indexExists(user.getId())) {
            log.warn("Пользователь не найден: {}", user);
            throw new UserNotFoundException();
        } else {
            storage.put(user);
            log.info("Пользователь добавлен: {}", user);
        }
        return user;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public Optional<User> getUser(Long id) {
        return storage.getUser(id);
    }

    public User addFriend(Long id, Long friendId) {
        var user1 = storage.getUser(id);
        var user2 = storage.getUser(friendId);
        if (user1.isPresent() && user2.isPresent()) {
            user1.get().addFriend(user2.get().getId());
            user2.get().addFriend(user1.get().getId());
            return user1.get();
        }
        throw new UserNotFoundException();
    }

    public User removeFriend(Long id, Long friendId) {
        var user1 = storage.getUser(id);
        var user2 = storage.getUser(friendId);
        if (user1.isPresent() && user2.isPresent()) {
            user1.get().removeFriend(user2.get().getId());
            user2.get().removeFriend(user1.get().getId());
            return user1.get();
        }
        throw new UserNotFoundException();
    }

    public Collection<User> getFriends(Long id) {
        var user = storage.getUser(id);
        if (user.isPresent()) {
            return user.get().getFriends().stream()
                    .map(storage::getUser)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toUnmodifiableList());
        }
        throw new UserNotFoundException();
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        var userOpt = storage.getUser(id);
        var otherOpt = storage.getUser(otherId);
        if (userOpt.isEmpty() || otherOpt.isEmpty()) {
            throw new UserNotFoundException();
        }
        var otherFriends = storage.getUser(otherId).get().getFriends();
        return userOpt.get().getFriends().stream()
                .filter(otherFriends::contains)
                .map(storage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public boolean userExists(Long id) {
        return storage.indexExists(id);
    }

    public static Optional<String> validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            return Optional.of("логин содержит пробел");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
            return Optional.of("неверная дата рождения");
        }
        return Optional.empty();
    }
}
