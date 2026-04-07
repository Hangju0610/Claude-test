package com.example.todo.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TodoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("R10: New todo should have completed=false by default")
    void newTodo_shouldHaveCompletedFalseByDefault() {
        Todo todo = new Todo();
        assertThat(todo.getCompleted()).isFalse();
    }

    @Test
    @DisplayName("R06: Todo with valid title should pass validation")
    void todo_withValidTitle_shouldPassValidation() {
        Todo todo = new Todo();
        todo.setTitle("Buy groceries");
        todo.setDescription("Milk, eggs, bread");

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("R06: Todo with blank title should fail validation")
    void todo_withBlankTitle_shouldFailValidation() {
        Todo todo = new Todo();
        todo.setTitle("");

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
    }

    @Test
    @DisplayName("R06: Todo with null title should fail validation")
    void todo_withNullTitle_shouldFailValidation() {
        Todo todo = new Todo();
        todo.setTitle(null);

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("R06: Todo with title exceeding 200 chars should fail validation")
    void todo_withTitleExceeding200Chars_shouldFailValidation() {
        Todo todo = new Todo();
        todo.setTitle("a".repeat(201));

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Todo with exactly 200 char title should pass validation")
    void todo_withExactly200CharTitle_shouldPassValidation() {
        Todo todo = new Todo();
        todo.setTitle("a".repeat(200));

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Todo description can be null")
    void todo_withNullDescription_shouldPassValidation() {
        Todo todo = new Todo();
        todo.setTitle("Valid title");
        todo.setDescription(null);

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);
        assertThat(violations).isEmpty();
    }
}
