package com.pairgood.todo_api.exception;

import com.pairgood.todo_api.todo.Todo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class TodoExceptionHandlerTest {

    private TodoExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new TodoExceptionHandler();
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException testException = new HttpMessageNotReadableException("test exception");

        ResponseEntity<Object> result = exceptionHandler.handleHttpMessageNotReadable(testException,
                null, null, null);

        assertEquals(BAD_REQUEST, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("Malformed JSON request", exception.getMessage());
    }

    @Test
    void shouldHandleEntityNotFound() {
        EntityNotFoundException testException = new EntityNotFoundException("test exception");

        ResponseEntity<Object> result = exceptionHandler.handlerEntityNotFound(testException);

        assertEquals(NOT_FOUND, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("test exception", exception.getMessage());
    }

    @Test
    void shouldHandleExceptionInternal() {
        Exception testException = new Exception("test exception");

        ResponseEntity<Object> result = exceptionHandler.handleExceptionInternal(testException,null,
                null, null, null);

        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("test exception", exception.getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException mockException = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult mockBindingResult = Mockito.mock(BindingResult.class);

        when(mockException.getMessage()).thenReturn("test exception message");
        when(mockException.getBindingResult()).thenReturn(mockBindingResult);
        when(mockBindingResult.getFieldErrors()).thenReturn(new ArrayList<>());

        ResponseEntity<Object> result = exceptionHandler.handleMethodArgumentNotValid(mockException,null,
                null, null);

        assertEquals(BAD_REQUEST, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("test exception message", exception.getMessage());
    }

    @Test
    void shouldHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException testException = Mockito.mock(MissingServletRequestParameterException.class);

        when(testException.getParameterName()).thenReturn("test parameter name");
        when(testException.getParameterType()).thenReturn("test parameter type");
        when(testException.getMessage()).thenReturn("test message");

        ResponseEntity<Object> result = exceptionHandler.handleMissingServletRequestParameter(testException,
                null, null, null);

        assertEquals(BAD_REQUEST, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("test message", exception.getMessage());
        assertEquals("test parameter name parameter missing. Parameter type: test parameter type", exception.getErrorMessage());
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException testException = new MethodArgumentTypeMismatchException("", Todo.class, "test object", null, null);

        ResponseEntity<Object> result = exceptionHandler.handleMethodArgumentTypeMismatch(testException, null);

        assertEquals(BAD_REQUEST, result.getStatusCode());

        TodoCustomException exception = (TodoCustomException) result.getBody();
        assertEquals("Failed to convert value of type 'java.lang.String' to required type 'com.pairgood.todo_api.todo.Todo'", exception.getMessage());
        assertEquals("test object should be of type: com.pairgood.todo_api.todo.Todo", exception.getErrorMessage());
    }
}