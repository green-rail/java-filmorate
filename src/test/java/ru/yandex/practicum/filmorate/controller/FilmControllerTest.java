package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
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
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void restartMockMvc() {
        MockMvcBuilders.standaloneSetup().build();
    }

    @Test
    public void filmControllerShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));
    }

    @Test
    public void getNonExisitingFilm() throws Exception {
        mockMvc.perform(get("/films/1")).andExpect(status().is(404));
    }

    @Test
    public void getFilmById() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(makeMovieWithName("Movie name")))
                .andExpect(status().is(201));

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void filmControllerBadRequestOnFailedValidation() throws Exception {
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(""))
                .andExpect(status().is(400));

        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(
                    "{\"name\": \"FilmName\",\n" +
                    "    \"description\": \"descriptionItem\",\n" +
                    "    \"duration\": 22,\n" +
                    "    \"releaseDate\": \"1012-04-23T18:25:43.511Z\"\n}"))
                .andExpect(status().is(400));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(
                                "{\"name\": \"\","
                                        + "\"description\": \"descriptionItem\","
                                        + "\"duration\": 22,"
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(
                                "{\"name\": \"FilmName\","
                                        + "\"description\": \"descriptionItem\","
                                        + "\"duration\": 0,"
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().is(400));
    }

    @Test
    public void filmControllerCreateValidAndUpdate() throws Exception {

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(
                                "{\"name\": \"FilmName\","
                                        + "\"description\": \"descriptionItem\","
                                        + "\"duration\": 22,"
                                        + "\"mpa\": { \"id\": 1}, "
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("FilmName"));

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(
                                "{\"id\": 9999, \"name\": \"FilmName\","
                                        + "\"description\": \"Updated description\","
                                        + "\"duration\": 120,"
                                        + "\"mpa\": { \"id\": 1}, "
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(
                                "{\"id\": 1, \"name\": \"FilmName\","
                                        + "\"description\": \"Updated description\","
                                        + "\"duration\": 120,"
                                        + "\"mpa\": { \"id\": 1}, "
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Updated description"))
                .andExpect(jsonPath("$[0].duration").value(120));
    }


    @Test
    public void putAndDeleteLikes() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(
                                "{\"name\": \"FilmName\","
                                        + "\"description\": \"descriptionItem\","
                                        + "\"duration\": 22,"
                                        + "\"mpa\": { \"id\": 1}, "
                                        + "\"releaseDate\": \"2012-04-23\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"login\": \"UserLogin\","
                                + "\"name\": \"Nick Name\","
                                + "\"email\": \"email@mail.ru\","
                                + "\"birthday\": \"1990-04-23\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/films/1/like/1")).andExpect(status().isOk());
        mockMvc.perform(put("/films/1/like/200")).andExpect(status().is(404));
        mockMvc.perform(put("/films/200/like/1")).andExpect(status().is(404));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likes[0]").value(1))
                .andExpect(jsonPath("$[0].likesCount").value(1));

        mockMvc.perform(delete("/films/1/like/1")).andExpect(status().isOk());
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likesCount").value(0))
                .andExpect(jsonPath("$[0].likes").value(Matchers.empty()));
    }

    @Test
    public void getMostPopular() throws Exception {

        mockMvc.perform(get("/films/popular"))
                .andExpect(jsonPath("$.length()").value(0));

        for (int i = 1; i <= 11; i++) {
            mockMvc.perform(post("/films")
                            .contentType("application/json")
                            .content(makeMovieWithName("Movie " + i)))
                    .andExpect(status().isCreated());
        }

        for (int i = 1; i <= 10; i++) {
            mockMvc.perform(post("/users")
                            .contentType("application/json")
                            .content(makeUserWithLogin("login" + i)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(put("/films/5/like/1")).andExpect(status().isOk());
        mockMvc.perform(put("/films/5/like/2")).andExpect(status().isOk());
        mockMvc.perform(put("/films/5/like/3")).andExpect(status().isOk());
        mockMvc.perform(put("/films/5/like/4")).andExpect(status().isOk());

        mockMvc.perform(put("/films/8/like/1")).andExpect(status().isOk());
        mockMvc.perform(put("/films/8/like/2")).andExpect(status().isOk());
        mockMvc.perform(put("/films/8/like/3")).andExpect(status().isOk());

        mockMvc.perform(put("/films/2/like/1")).andExpect(status().isOk());
        mockMvc.perform(put("/films/2/like/2")).andExpect(status().isOk());

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Movie 5"))
                .andExpect(jsonPath("$[0].likesCount").value(4))
                .andExpect(jsonPath("$[1].name").value("Movie 8"))
                .andExpect(jsonPath("$[1].likesCount").value(3));

        mockMvc.perform(get("/films/popular"))
                .andExpect(jsonPath("$.length()").value(10));

        mockMvc.perform(get("/films/popular?count=100"))
                .andExpect(jsonPath("$.length()").value(11));
    }

    private static String makeMovieWithName(String name) {
        return String.format(
                 "{\"name\": \"%s\","
                + "\"description\": \"descriptionItem\","
                + "\"duration\": 22,"
                + "\"mpa\": { \"id\": 1}, "
                + "\"releaseDate\": \"2012-04-23\"}", name);
    }

    private static String makeUserWithLogin(String login) {
        return String.format(
                "{\"login\": \"%s\","
                + "\"name\": \"Nick Name\","
                + "\"email\": \"email@mail.ru\","
                + "\"birthday\": \"1990-04-23\"}",
                login);
    }

}