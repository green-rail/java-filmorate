package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.addUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        if (!userService.userExists(id)) {
            throw new UserNotFoundException();
        }
        var user = userService.getUser(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        if (!(userService.userExists(id) && userService.userExists(friendId))) {
            throw new UserNotFoundException();
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        if (!(userService.userExists(id) && userService.userExists(friendId))) {
            throw new UserNotFoundException();
        }
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable("id") Long id) throws ValidationException {
        if (!userService.userExists(id)) {
            throw new UserNotFoundException();
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Long id,
                                             @PathVariable("otherId") Long otherId) {
        if (!(userService.userExists(id) && userService.userExists(otherId))) {
            throw new UserNotFoundException();
        }
        return userService.getCommonFriends(id, otherId);
    }

}
