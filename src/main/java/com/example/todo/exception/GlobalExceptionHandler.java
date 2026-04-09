package com.example.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        com.example.todo.controller.TodoApiController.class,
        com.example.todo.controller.MemberApiController.class
})
public class GlobalExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTodoNotFound(TodoNotFoundException ex) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMemberNotFound(MemberNotFoundException ex) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateMember(DuplicateMemberException ex) {
        return ErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation failed");
    }
}
