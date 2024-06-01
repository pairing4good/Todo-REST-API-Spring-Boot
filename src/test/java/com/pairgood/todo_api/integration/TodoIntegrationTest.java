package com.pairgood.todo_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pairgood.todo_api.config.LoggingConfiguration;
import com.pairgood.todo_api.todo.Todo;
import com.pairgood.todo_api.todo.TodoController;
import com.pairgood.todo_api.todo.TodoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.slf4j.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(TodoController.class)
@Import({LoggingConfiguration.class})
public class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService service;

    @MockBean
    private Logger logger;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldReturnServerErrorForMissingEndpoints()  throws Exception {
        mockMvc.perform(get("/bad")).andDo(print()).andExpect(status().is5xxServerError());
    }

    @Test
    void shouldCreateTodo_WhenTodoSuccessfullySaved() throws Exception{
        long savedId = 1L;

        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.AddItemToThelist(any(Todo.class))).thenReturn(savedId);

        this.mockMvc.perform(post("/api/v1/todo/additem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().json("{" +
                        "\"message\":\"Item added to todo list\"," +
                        "\"code\":201," +
                        "\"httpStatus\":\"CREATED\"" +
                        "}"));

        verify(logger).info("Item added to todo list. code: 201");
    }

    @Test
    void shouldNotCreateTodo_WhenTodoUnsuccessfullySaved() throws Exception{
        long unsavedIndicator = 0L;

        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.AddItemToThelist(any(Todo.class))).thenReturn(unsavedIndicator);

        this.mockMvc.perform(post("/api/v1/todo/additem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"message\":\"Item Not added to todo list\"," +
                        "\"code\":400," +
                        "\"httpStatus\":\"BAD_REQUEST\"" +
                        "}"));

        verify(logger).info("Item Not added to todo list. code: 400");
    }

    @Test
    void shouldUpdateTodo_WhenTodoExists() throws Exception{
        long savedId = 1L;

        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.isTodoItemIdValid(anyLong())).thenReturn(true);
        when(service.UpdateTodoItem(anyLong(), any(Todo.class))).thenReturn(savedId);

        this.mockMvc.perform(put("/api/v1/todo/updateitem/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"message\":\"Item with the following title test title updated\"," +
                        "\"code\":200," +
                        "\"httpStatus\":\"OK\"" +
                        "}"));

        verify(logger).info("Item with the following title test title updated. code: 200");
    }

    @Test
    void shouldNotUpdateTodo_WhenTodoIsInvalid() throws Exception{
        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.isTodoItemIdValid(anyLong())).thenReturn(false);

        this.mockMvc.perform(put("/api/v1/todo/updateitem/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().json("{" +
                        "\"message\":\"Request not successful, invalid information provided. Please try again.\"," +
                        "\"code\":404," +
                        "\"httpStatus\":\"NOT_FOUND\"" +
                        "}"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldNotUpdateTodo_WhenTodoIsValid_ButNoTodoExists() throws Exception{
        long unsavedIndicator = 0L;

        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
        String todoString = mapper.writeValueAsString(todo);

        when(service.isTodoItemIdValid(anyLong())).thenReturn(true);
        when(service.UpdateTodoItem(anyLong(), any(Todo.class))).thenReturn(unsavedIndicator);

        this.mockMvc.perform(put("/api/v1/todo/updateitem/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoString))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"message\":\"Item Not updated\"," +
                        "\"code\":400," +
                        "\"httpStatus\":\"BAD_REQUEST\"" +
                        "}"));

        verify(logger).info("Item Not updated. code: 400");
    }

    @Test
    void shouldDelete_WhenTodoIsValid() throws Exception{
        when(service.isTodoItemIdValid(anyLong())).thenReturn(true);

        this.mockMvc.perform(delete("/api/v1/todo/deleteitem/1"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"message\":\"Item deleted\"," +
                        "\"code\":200," +
                        "\"httpStatus\":\"OK\"" +
                        "}"));

        verify(logger).info("Item deleted. code: 200");
    }

    @Test
    void shouldNotDelete_WhenTodoIsInValid() throws Exception{
        when(service.isTodoItemIdValid(anyLong())).thenReturn(false);

        this.mockMvc.perform(delete("/api/v1/todo/deleteitem/1"))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().json("{" +
                        "\"message\":\"Request not successful, invalid information provided. Please try again.\"," +
                        "\"code\":404," +
                        "\"httpStatus\":\"NOT_FOUND\"" +
                        "}"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldListTodos_WhenTodosFound() throws Exception{
        Todo todo = new Todo(0, "test title", "test description",
                false, LocalDate.of(2024, 4, 24), null, null);
        when(service.getMyTodoList()).thenReturn(List.of(todo));

        this.mockMvc.perform(get("/api/v1/todo/todolist"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[" +
                            "{" +
                                "\"todoTitle\":\"test title\"," +
                                "\"todoDescription\":\"test description\"," +
                                "\"todoDate\":\"2024-04-24\"," +
                                "\"creationDate\":null," +
                                "\"updateDate\":null," +
                                "\"todoId\":0," +
                                "\"complete\":false" +
                            "}" +
                        "]"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldListTodos_WhenNoTodosFound() throws Exception{
        when(service.getMyTodoList()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/v1/todo/todolist"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldCountTodos_WhenTodosFound() throws Exception{
        when(service.getNumberTodoItem()).thenReturn(1L);

        this.mockMvc.perform(get("/api/v1/todo/todocount"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("1"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldCountTodos_WhenNoTodosFound() throws Exception{
        when(service.getNumberTodoItem()).thenReturn(0L);

        this.mockMvc.perform(get("/api/v1/todo/todocount"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("0"));

        verifyNoInteractions(logger);
    }

    @Test
    void shouldHandleHttpMessageNotReadable() throws Exception{
        when(service.getNumberTodoItem()).thenThrow(new HttpMessageNotReadableException("test message"));

        this.mockMvc.perform(get("/api/v1/todo/todocount"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus", org.hamcrest.Matchers.is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("Malformed JSON request")))
                .andExpect(jsonPath("$.errorMessage", org.hamcrest.Matchers.is("test message")));
    }

    @Test
    void shouldHandleEntityNotFound() throws Exception{
        when(service.getNumberTodoItem()).thenThrow(new EntityNotFoundException("test message"));

        this.mockMvc.perform(get("/api/v1/todo/todocount"))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus", org.hamcrest.Matchers.is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.is("test message")));
    }
}