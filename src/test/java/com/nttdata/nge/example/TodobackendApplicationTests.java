package com.nttdata.nge.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.fail;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.nge.example.server.NgeExampleServer;
import com.nttdata.nge.example.server.model.Todo;

@SpringBootTest(classes = NgeExampleServer.class)
public class TodobackendApplicationTests extends AbstractTestNGSpringContextTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeClass
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testPingHomeUrl() throws Exception {
		mockMvc.perform(get("/home")).andExpect(content().string("home")).andExpect(status().isOk());
	}

	@Test
	public void addAndRead() throws Exception {

		mockMvc.perform(post("/todos").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(new Todo(0, "testNg", false, 1)))).andExpect(status().isOk());

		String response = mockMvc.perform(get("/todos").contentType(MediaType.APPLICATION_JSON)).andReturn()
				.getResponse().getContentAsString();

		Todo[] todos = fromJsonString(response, Todo[].class);
		if (Arrays.stream(todos).noneMatch(t -> t.getTitle().equals("testNg"))) {
			fail("The result should contain the addded testNg todo");
		}

	}

	public String asJsonString(final Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T fromJsonString(final String in, Class<T> c) {
		try {
			return objectMapper.readValue(in, c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
