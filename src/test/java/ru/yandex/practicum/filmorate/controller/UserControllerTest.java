package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

class UserControllerTest {

    private static Validator validator;
    private Set<ConstraintViolation<User>> violations;

    @BeforeAll
    static void makeValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static User makeValidUser() {
        return User.builder()
                .id(0)
                .email("zx@yandex.ru")
                .login("user_01")
                .name("Tom")
                .birthday(LocalDate.of(1993, 1, 1))
                .build();
    }

    @Test
    void validUserShouldntThrow() {
        var user = makeValidUser();
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Корректный пользователь не прошел валидацию.");
        assertTrue(UserController.validateUser(user), "Корректный пользователь не прошел валидацию.");
    }


    @Test
    void annotationValidationsTest() {
        //var user = new User(-1, "invalidemail.ru", "");
        var user = User.builder()
                .id(-1)
                .email("invalidemail.ru")
                .login("")
                .build();
        violations = validator.validate(user);
        assertEquals(3, violations.size(), "Неверное число ошибок валидации.");
    }

    @Test
    void controllerValidationTest() {
        //var user = new User(0, "zx@yandex.ru", " invalidLogin");
        var user = User.builder()
                .id(0)
                .email("zx@yandex.ru")
                .login(" invalidLogin")
                .build();
        assertFalse(UserController.validateUser(user), "Некорректный пользователь прошел валидацию.");
        user = makeValidUser();
        user.setBirthday(LocalDateTime.now().plusDays(1).toLocalDate());
        assertFalse(UserController.validateUser(user), "Некорректный пользователь прошел валидацию.");
    }
}