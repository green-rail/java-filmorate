package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
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
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (!validateUser(user)) {
            throw new ValidationException();
        }
        User newUser;
        if (!users.containsKey(user.getId())) {
            newUser = user.withId(++idCounter);
            var name = user.getName() == null || user.getName().isBlank() ?
                    user.getLogin() : user.getName();
            newUser.setName(name);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        } else {
            users.put(user.getId(), user);
            log.info("Пользователь добавлен: {}", user);
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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
