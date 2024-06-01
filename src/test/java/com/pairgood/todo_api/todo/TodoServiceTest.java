package com.pairgood.todo_api.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.slf4j.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private Logger logger;
    @Mock
    private TodoRepository repository;
    @InjectMocks
    private TodoService service;

    private List<Todo> todos;
    private Todo firstTodo;
    private Todo secondTodo;
    private Todo thirdTodo;

    @BeforeEach
    void setUp(){
        firstTodo = new Todo(1L, "first title", "first description",
                false, LocalDate.now(), null, null);
        secondTodo = new Todo(2L, "second title", "second description",
                false, LocalDate.now(), null, null);
        thirdTodo = new Todo(3L, "third title", "third description",
                false, LocalDate.now(), null, null);

        todos = List.of(firstTodo, secondTodo, thirdTodo);
    }

    @Test
    void shouldListAllTodos() {
        when(repository.findAll()).thenReturn(todos);

        List<Todo> myTodoList = service.getMyTodoList();

        assertEquals(3, myTodoList.size());
        assertEquals(1, myTodoList.get(0).getTodoId());
        assertEquals(2, myTodoList.get(1).getTodoId());
        assertEquals(3, myTodoList.get(2).getTodoId());

        verifyNoInteractions(logger);
    }

    @Test
    void shouldSaveTodo() {
        long savedTodoId = service.AddItemToThelist(firstTodo);

        assertEquals(firstTodo.getTodoId(), savedTodoId);

        verify(repository).save(firstTodo);
        verifyNoInteractions(logger);
    }

    @Test
    void deleteItem() {
        service.DeleteItem(1L);

        verify(repository).deleteById(1L);
        verify(logger).info("Item removed from the list");
    }

    @Test
    void shouldUpdateTitle() {
        Todo updatedTodo = new Todo(1L, "updated first title", "first description",
                false, LocalDate.now(), null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(firstTodo));

        long result = service.UpdateTodoItem(1L, updatedTodo);

        assertEquals(1L, result);
        assertEquals(updatedTodo.getTodoTitle(), firstTodo.getTodoTitle());

        verify(repository).save(firstTodo);
        verifyNoInteractions(logger);
    }

    @Test
    void shouldUpdateDescription() {
        Todo updatedTodo = new Todo(1L, "first title", "updated first description",
                false, LocalDate.now(), null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(firstTodo));

        long result = service.UpdateTodoItem(1L, updatedTodo);

        assertEquals(1L, result);
        assertEquals(updatedTodo.getTodoDescription(), firstTodo.getTodoDescription());

        verify(repository).save(firstTodo);
        verifyNoInteractions(logger);
    }

    @Test
    void shouldUpdateCompletionStatus() {
        Todo updatedTodo = new Todo(1L, "first title", "first description",
                true, LocalDate.now(), null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(firstTodo));

        long result = service.UpdateTodoItem(1L, updatedTodo);

        assertEquals(1L, result);
        assertEquals(updatedTodo.isComplete(), firstTodo.isComplete());

        verify(repository).save(firstTodo);
        verifyNoInteractions(logger);
    }

    @Test
    void shouldUpdateTodoDate() {
        Todo updatedTodo = new Todo(1L, "first title", "first description",
                false, LocalDate.of(1, 1, 1), null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(firstTodo));

        long result = service.UpdateTodoItem(1L, updatedTodo);

        assertEquals(1L, result);
        assertEquals(updatedTodo.getTodoDate(), firstTodo.getTodoDate());

        verify(repository).save(firstTodo);
        verifyNoInteractions(logger);
    }

    @Test
    void shouldNotBlowUpIfUpdatingFails() {
        Todo updatedTodo = new Todo();

        when(repository.findById(1L)).thenReturn(Optional.of(firstTodo));

        IllegalStateException expectedException = new IllegalStateException("test exception");
        when(repository.save(firstTodo)).thenThrow(expectedException);

        long result = service.UpdateTodoItem(1L, updatedTodo);

        assertEquals(0L, result);
        verify(logger).error("Unable to update todo {}", 1L, expectedException);
    }

    @Test
    void shouldBeValid_WhenTodoIdIsPresent() {
        Optional present = Optional.of(firstTodo);
        when(repository.findById(1L)).thenReturn(present);

        boolean result = service.isTodoItemIdValid(1L);

        assertTrue(result);
    }

    @Test
    void shouldNotBeValid_WhenTodoIdIsNotPresent() {
        Optional missing = Optional.ofNullable(null);
        when(repository.findById(1L)).thenReturn(missing);

        boolean result = service.isTodoItemIdValid(1L);

        assertFalse(result);
    }

    @Test
    void shouldReturnTheNumberOfTodos() {
        when(repository.count()).thenReturn(3L);

        long results = service.getNumberTodoItem();

        assertEquals(3, results);
    }
}