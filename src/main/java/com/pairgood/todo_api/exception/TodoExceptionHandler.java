package com.pairgood.todo_api.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class TodoExceptionHandler extends ResponseEntityExceptionHandler {

    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String error = "Malformed JSON request";

        return objectResponseEntity(new TodoCustomException(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handlerEntityNotFound(EntityNotFoundException ex) {
        TodoCustomException TodoCustomException = new TodoCustomException(HttpStatus.NOT_FOUND);
        TodoCustomException.setMessage(ex.getMessage());


        return objectResponseEntity(TodoCustomException);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


        TodoCustomException TodoCustomException = new TodoCustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        TodoCustomException.setMessage(ex.getMessage());

        return objectResponseEntity(TodoCustomException);
    }

    @SuppressWarnings({"NullableProblems", "MismatchedQueryAndUpdateOfCollection"})
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        TodoCustomException TodoCustomException = new TodoCustomException(HttpStatus.BAD_REQUEST);

        TodoCustomException.setMessage(ex.getMessage());


        return objectResponseEntity(TodoCustomException);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        TodoCustomException TodoCustomException = new TodoCustomException(HttpStatus.BAD_REQUEST);
        String error = ex.getParameterName() + " parameter missing. Parameter type: " + ex.getParameterType();
        TodoCustomException.setMessage(ex.getMessage());
        TodoCustomException.setErrorMessage(error);


        return objectResponseEntity(TodoCustomException);
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " should be of type: " + Objects.requireNonNull(ex.getRequiredType()).getName();

        TodoCustomException TodoCustomException = new TodoCustomException(HttpStatus.BAD_REQUEST);

        TodoCustomException.setMessage(ex.getMessage());
        TodoCustomException.setErrorMessage(error);


        return objectResponseEntity(TodoCustomException);

    }

    private ResponseEntity<Object> objectResponseEntity(TodoCustomException TodoCustomException) {
        return new ResponseEntity<>(TodoCustomException, TodoCustomException.getHttpStatus());
    }
}
