package com.pairgood.todo_api.integration;

import com.pairgood.todo_api.todo.Todo;
import com.pairgood.todo_api.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RepositoryIntegrationTest {

    @SuppressWarnings("unused")
    @Autowired
    private TodoRepository repository;

    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo(0, "test title", "test description",
                false, LocalDate.now(), null, null);
    }

    @Test
    void shouldGenerateIdStartingAt1110_WhenFirstTodoSaved() {
        Todo savedTodo = repository.save(todo);

        assertTrue(savedTodo.getTodoId() >= 1110);
    }

    @Test
    void shouldFailToSave_WhenTitleLengthLessThanFiveCharactersLong() {
        todo.setTodoTitle("1234");

        try {
            repository.save(todo);
            fail();
        } catch (Exception e) {
            assertInstanceOf(TransactionSystemException.class, e);

            StringWriter strOut = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strOut);
            e.printStackTrace(printWriter);

            assertTrue(strOut.toString().contains("A Title should have at least 5 characters"),
                    "TransactionSystemException was not caused by the todo title length.");
        }
    }

    @Test
    void shouldFailToSave_WhenDescriptionLengthLessThanFiveCharactersLong() {
        todo.setTodoDescription("1234");

        try {
            repository.save(todo);
            fail();
        } catch (Exception e) {
            assertInstanceOf(TransactionSystemException.class, e);

            StringWriter strOut = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strOut);
            e.printStackTrace(printWriter);

            assertTrue(strOut.toString().contains("A Description should have at least 5 characters"),
                    "TransactionSystemException was not caused by the todo description length.");
        }
    }
}
