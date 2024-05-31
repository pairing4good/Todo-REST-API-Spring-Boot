package com.pairgood.todo_api;

import com.pairgood.todo_api.todo.ResponseTodoList;
import com.pairgood.todo_api.todo.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.HttpStatus.*;


@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT)
public class TodoApiApplicationTests {

	@SuppressWarnings("unused")
    @LocalServerPort
	private int port;

	@SuppressWarnings("unused")
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldAddTodoSuccessfully(){
		Todo todo = new Todo(0, "test todo title", "test todo description",
				false, LocalDate.now(), null, null);

		ResponseEntity<ResponseTodoList> response = restTemplate
				.postForEntity("http://localhost:" + port + "/api/v1/todo/additem", todo, ResponseTodoList.class);

		assertEquals(201, response.getStatusCode().value());

		ResponseTodoList body = response.getBody();
		assert body != null;
        assertEquals(201, body.getCode());
		assertEquals("Item added to todo list", body.getMessage());
		assertEquals(CREATED, body.getHttpStatus());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
    @Test
	void shouldListAtLeastOneTodo(){
		addTodo(2);
		List<LinkedHashMap> response = restTemplate
				.getForObject("http://localhost:" + port + "/api/v1/todo/todolist", List.class);

        assertFalse(response.isEmpty());
		assertEquals(1110, response.get(0).get("todoId"));
	}

	@Test
	void shouldDeleteTodoSuccessfully(){
		addTodo(3);

		long todoCountBefore = lookupTodoCount();

		restTemplate.delete("http://localhost:" + port + "/api/v1/todo/deleteitem/1110");

		long todoCountAfter = lookupTodoCount();

		assertEquals(todoCountBefore - 1, todoCountAfter);
	}

	@Test
	void shouldUpdateTodo() throws ParseException {
		addTodo(4);

		Todo todo = findFirstTodo();
		todo.setTodoTitle("updated title");

		restTemplate.put("http://localhost:" + port + "/api/v1/todo/updateitem/" + todo.getTodoId(), todo);

		Todo updatedTodo = findFirstTodo();
		assertEquals("updated title", updatedTodo.getTodoTitle());
	}

	@Test
	void shouldCountTodos(){
		addTodo(4);

		long response = restTemplate
				.getForObject("http://localhost:" + port + "/api/v1/todo/todocount", Long.class);

		assertTrue(response > 0);
	}

	private long lookupTodoCount(){
        return restTemplate
				.getForObject("http://localhost:" + port + "/api/v1/todo/todocount", Long.class);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
    private Todo findFirstTodo() throws ParseException {
		List<LinkedHashMap> response = restTemplate
				.getForObject("http://localhost:" + port + "/api/v1/todo/todolist", List.class);
		LinkedHashMap todoMap = response.get(0);
		LocalDate todoDate = LocalDate.parse((String) todoMap.get("todoDate"));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
		Date creationDate = formatter.parse((String) todoMap.get("creationDate"));
		Date updateDate = formatter.parse((String) todoMap.get("updateDate"));
        return new Todo((Integer) todoMap.get("todoId"), (String) todoMap.get("todoTitle"),
				(String) todoMap.get("todoDescription"), (Boolean) todoMap.get("complete"),
				todoDate, creationDate, updateDate);
	}

	private void addTodo(int sequence){
		Todo todo = new Todo(0, "test todo title " + sequence, "test todo description " + sequence,
				false, LocalDate.now(), null, null);

		ResponseEntity<ResponseTodoList> response = restTemplate
				.postForEntity("http://localhost:" + port + "/api/v1/todo/additem", todo, ResponseTodoList.class);

		assertEquals(201, response.getStatusCode().value());

		ResponseTodoList body = response.getBody();
        assert body != null;
        assertEquals(201, body.getCode());
	}
}
