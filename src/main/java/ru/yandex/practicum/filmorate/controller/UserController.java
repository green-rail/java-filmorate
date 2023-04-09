package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (!validateUser(user)) {
            throw new ValidationException();
        }
        User newUser;
        if (!users.containsKey(user.getId())) {
            newUser = user.withId(++idCounter);
            //newUser = new User(idCounter++, user.getEmail(), user.getLogin());
            var name = user.getName() == null || user.getName().isBlank() ?
                    user.getLogin() : user.getName();
            newUser.setName(name);
            //newUser.setBirthday(user.getBirthday());
            users.put(newUser.getId(), newUser);
            log.info("Пользователь добавлен: {}", user);
        } else {
            log.warn("Пользователь уже существует: {}", user);
            throw new ValidationException();
        }
        return newUser;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (!validateUser(user)) {
            throw new ValidationException();
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден: {}", user);
            throw new ValidationException();
        } else {
            users.put(user.getId(), user);
            log.info("Пользователь добавлен: {}", user);
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return users.values().stream().toList();
    }


    static boolean validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            return false;
        }
        if (user.getBirthday().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
            return false;
        }

        return true;
    }
}
