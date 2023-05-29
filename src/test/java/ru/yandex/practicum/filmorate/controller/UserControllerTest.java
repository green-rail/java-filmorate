package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void restartMockMvc() {
        MockMvcBuilders.standaloneSetup().build();
    }

    @Test
    public void userControllerShouldReturnEmptyArray() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }

    @Test
    public void getNonExistingUserShouldBe404() throws Exception {
        mockMvc.perform(get("/users/12"))
                .andExpect(status().is(404));
    }

    @Test
    public void getUserById() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("login", "name", "email@mail.ru")))
                .andExpect(status().is(201));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().is(200));
    }

    @Test
    public void userControllerBadRequestOnFailedValidation() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("Login withspace", "Username", "email@mail.ru")))
                .andExpect(status().is(400));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"login\": \"UserLogin\","
                                + "\"name\": \"Nick Name\","
                                + "\"email\": \"emailmail.ru\","
                                + "\"birthday\": \"1990-04-23\"}"))
                .andExpect(status().is(400));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"login\": \"UserLogin\","
                                + "\"name\": \"Nick Name\","
                                + "\"email\": \"emailmail.ru\","
                                + "\"birthday\": \"2990-04-23\"}"))
                .andExpect(status().is(400));
    }

    @Test
    public void userControllerCreateValidAndUpdate() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("UserLogin", "User Name", "email@mail.ru")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].login").value("UserLogin"));

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content("{\"id\": 2222,\"login\": \"UserLogin\","
                                + "\"name\": \"Updated Name\","
                                + "\"email\": \"email@mail.ru\","
                                + "\"birthday\": \"1995-04-23\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content("{\"id\": 1,\"login\": \"UserLogin\","
                                + "\"name\": \"Updated Name\","
                                + "\"email\": \"email@mail.ru\","
                                + "\"birthday\": \"1995-04-23\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Updated Name"))
                .andExpect(jsonPath("$[0].birthday").value("1995-04-23"));
    }

    @Test
    public void getFriendsForNonExisitingIds() throws Exception {
        mockMvc.perform(get("/users/1/friends")).andExpect(status().is(404));
        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().is(404));
        mockMvc.perform(delete("/users/1/friends/2")).andExpect(status().is(404));
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("UserLogin1", "User Name", "email@mail.ru")))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().is(404));
        mockMvc.perform(delete("/users/1/friends/2")).andExpect(status().is(404));
    }

    @Test
    public void createUserWithFriendsShouldSucceed() throws Exception {
        createUserWithFriends();
    }

    @Test
    public void getAndDeleteFriends() throws Exception {

        createUserWithFriends();

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Friend 3"));
    }

    public void getCommonFriends() throws Exception {
        createUserWithFriends();

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/users/1/friends/3"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void createUserWithFriends() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("UserLogin1", "User Name", "email1@mail.ru")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("FriendLogin2", "Friend 2", "email2@mail.ru")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("FriendLogin3", "Friend 3", "email3@mail.ru")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(makeUserString("FriendLogin4", "Friend 4", "email4@mail.ru")))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());
        mockMvc.perform(put("/users/1/friends/3")).andExpect(status().isOk());

        mockMvc.perform(put("/users/2/friends/3")).andExpect(status().isOk());
        mockMvc.perform(put("/users/2/friends/4")).andExpect(status().isOk());

    }


    private static String makeUserString(String login, String name, String email) {
        return String.format(
                 "{\"login\": \"%s\","
                + "\"name\": \"%s\","
                + "\"email\": \"%s\","
                + "\"birthday\": \"1990-04-23\"}", login, name, email);
    }
}