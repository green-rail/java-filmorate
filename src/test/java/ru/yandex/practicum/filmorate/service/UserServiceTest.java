package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {

    private UserService service;

    @BeforeEach
    void makeService() {
        service = new UserService(new InMemoryUserStorage());
    }

    @Test
    void addUser() {
        var user = service.addUser(makeUser("login", "name"));

        assertEquals(1, service.getUsers().stream().count(), "Пользователь не добавлен.");
        assertEquals(user, service.getUsers().stream().findFirst().get(), "Пользователь не добавлен.");
    }

    @Test
    void updateUser() {
        var user = service.addUser(makeUser("login", "name"));
        assertEquals(1, service.getUsers().stream().count(), "Пользователь не добавлен.");
        var updatedUser = user.withId(user.getId());
        updatedUser.setName("Updated name");
        service.updateUser(updatedUser);
        assertEquals("Updated name", service.getUsers().stream().findFirst().get().getName(), "Пользователь не обновлен.");
    }

    private User makeUser(String login, String name) {
        var bDay = LocalDate.of(1990, 1, 1);
        return User.builder().login(login).email("e@mail.ru").name(name).birthday(bDay).build();
    }

    @Test
    void getUsers() {
        var user1 = service.addUser(makeUser("user1", "Name1"));
        var user2 = service.addUser(makeUser("user2", "Name2"));
        assertEquals(2, service.getUsers().stream().count(), "Неверное количество пользователей.");
        assertIterableEquals(List.of(user1, user2), service.getUsers(), "Неверное количество пользователей.");
    }

    @Test
    void addAndRemoveFriends() {
        var user1 = service.addUser(makeUser("user1", "Name1"));
        var user2 = service.addUser(makeUser("user2", "Name2"));
        service.addFriend(user1.getId(), user2.getId());
        assertEquals(user2.getId(), user1.getFriends().stream().findFirst().get(), "Неверный индекс друга.");
        assertEquals(user1.getId(), user2.getFriends().stream().findFirst().get(), "Неверный индекс друга.");
        service.removeFriend(user1.getId(), user2.getId());
        assertTrue(user1.getFriends().isEmpty(), "Друг не удален.");
        assertTrue(user2.getFriends().isEmpty(), "Друг не удален.");
    }


    @Test
    void getCommonFriends() throws ValidationException {
        var storage = new InMemoryUserStorage();
        var userService = new UserService(storage);

        var bDay = LocalDate.of(1990, 1, 1);

        var user1   = userService.addUser(User.builder().login("user1").email("e@mail.ru").name("j").birthday(bDay).build());
        var friend1 = userService.addUser(User.builder().login("friend1").email("e@mail.ru").name("j").birthday(bDay).build());
        var friend2 = userService.addUser(User.builder().login("friend2").email("e@mail.ru").name("j").birthday(bDay).build());
        var friend3 = userService.addUser(User.builder().login("friend3").email("e@mail.ru").name("j").birthday(bDay).build());
        var friend4 = userService.addUser(User.builder().login("friend4").email("e@mail.ru").name("j").birthday(bDay).build());
        var user2   = userService.addUser(User.builder().login("user2").email("e@mail.ru").name("j").birthday(bDay).build());
        userService.addFriend(user1.getId(), friend4.getId());
        userService.addFriend(user1.getId(), friend2.getId());
        userService.addFriend(user1.getId(), friend1.getId());

        userService.addFriend(user2.getId(), friend2.getId());
        userService.addFriend(user2.getId(), friend3.getId());
        userService.addFriend(user2.getId(), friend4.getId());

        assertIterableEquals(List.of(friend2, friend4),
                userService.getCommonFriends(user1.getId(), user2.getId()), "Неверные общие друзья.");
    }
}
