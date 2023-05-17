package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerValidationTest {
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
        assertTrue(UserService.validateUser(user).isEmpty(), "Корректный пользователь не прошел валидацию.");
    }


    @Test
    void annotationValidationsTest() {
        var user = User.builder()
                .email("invalidemail.ru")
                .login("")
                .build();
        violations = validator.validate(user);
        assertEquals(2, violations.size(), "Неверное число ошибок валидации.");
    }

    @Test
    void controllerValidationTest() {
        var user = User.builder()
                .id(0)
                .email("zx@yandex.ru")
                .login(" invalidLogin")
                .build();
        assertTrue(UserService.validateUser(user).isPresent(), "Некорректный пользователь прошел валидацию.");
        user = makeValidUser();
        user.setBirthday(LocalDateTime.now().plusDays(1).toLocalDate());
        assertTrue(UserService.validateUser(user).isPresent(), "Некорректный пользователь прошел валидацию.");
    }
}
