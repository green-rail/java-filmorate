package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void filmControllerShouldReturnEmptyList() throws Exception {
		mockMvc.perform(get("/films")).andExpect(status().isOk())
				.andExpect(content().string(equalTo("[]")));
	}

	@Test
	public void filmControllerBadRequestOnFailedValidation() throws Exception {
		mockMvc.perform(post("/films")
				.contentType("application/json")
				.content("")).andExpect(status().isBadRequest());

		mockMvc.perform(post("/films")
				.contentType("application/json")
				.content(
						"{\n" +
						"    \"name\": \"FilmName\",\n" +
						"    \"description\": \"descriptionItem\",\n" +
						"    \"duration\": 22,\n" +
						"    \"releaseDate\": \"1012-04-23T18:25:43.511Z\"\n" +
						"}")).andExpect(status().isBadRequest());

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
						.andExpect(status().isBadRequest());
	}

	@Test
	public void filmControllerCreateValidAndUpdate() throws Exception {

		mockMvc.perform(post("/films")
						.contentType("application/json")
						.content(
							"{\"name\": \"FilmName\","
							+ "\"description\": \"descriptionItem\","
							+ "\"duration\": 22,"
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
								+ "\"releaseDate\": \"2012-04-23\"}"))
						.andExpect(status().isNotFound());
		mockMvc.perform(put("/films")
						.contentType("application/json")
						.content(
								"{\"id\": 1, \"name\": \"FilmName\","
										+ "\"description\": \"Updated description\","
										+ "\"duration\": 120,"
										+ "\"releaseDate\": \"2012-04-23\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/films"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("Updated description"))
				.andExpect(jsonPath("$[0].duration").value(120));
	}

	@Test
	public void userControllerShouldReturnEmptyArray() throws Exception {
		mockMvc.perform(get("/users")).andExpect(status().isOk())
				.andExpect(content().string(equalTo("[]")));
	}

	@Test
	public void userControllerBadRequestOnFailedValidation() throws Exception {

		mockMvc.perform(post("/films")
				.contentType("application/json")
				.content("")).andExpect(status().isBadRequest());

		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content("{\"login\": \"User Login\","
								+ "\"name\": \"Nick Name\","
								+ "\"email\": \"email@mail.ru\","
								+ "\"birthday\": \"1990-04-23\"}"))
						.andExpect(status().isBadRequest());

		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content("{\"login\": \"UserLogin\","
								+ "\"name\": \"Nick Name\","
								+ "\"email\": \"emailmail.ru\","
								+ "\"birthday\": \"1990-04-23\"}"))
						.andExpect(status().isBadRequest());

		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content("{\"login\": \"UserLogin\","
								+ "\"name\": \"Nick Name\","
								+ "\"email\": \"emailmail.ru\","
								+ "\"birthday\": \"2990-04-23\"}"))
						.andExpect(status().isBadRequest());
	}

	@Test
	public void userControllerCreateValidAndUpdate() throws Exception {
		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content("{\"login\": \"UserLogin\","
								+ "\"name\": \"Nick Name\","
								+ "\"email\": \"email@mail.ru\","
								+ "\"birthday\": \"1990-04-23\"}"))
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
}
