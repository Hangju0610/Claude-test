package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo createTodo(Long id, String title, String description, boolean completed) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        return todo;
    }

    @Test
    @DisplayName("R01: createTodo should save and return TodoResponse")
    void createTodo_shouldSaveAndReturnResponse() {
        TodoRequest request = new TodoRequest();
        request.setTitle("New todo");
        request.setDescription("Description");

        Todo saved = createTodo(1L, "New todo", "Description", false);
        given(todoRepository.save(any(Todo.class))).willReturn(saved);

        TodoResponse response = todoService.createTodo(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("New todo");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getCompleted()).isFalse();
        then(todoRepository).should().save(any(Todo.class));
    }

    @Test
    @DisplayName("R02: getAllTodos should return list of TodoResponse")
    void getAllTodos_shouldReturnAllTodos() {
        List<Todo> todos = List.of(
                createTodo(1L, "Todo 1", null, false),
                createTodo(2L, "Todo 2", "Desc", true)
        );
        given(todoRepository.findAll()).willReturn(todos);

        List<TodoResponse> responses = todoService.getAllTodos();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Todo 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("Todo 2");
    }

    @Test
    @DisplayName("R02: getAllTodos should return empty list when no todos")
    void getAllTodos_shouldReturnEmptyList_whenNoTodos() {
        given(todoRepository.findAll()).willReturn(List.of());

        List<TodoResponse> responses = todoService.getAllTodos();

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("R03: getTodoById should return TodoResponse when found")
    void getTodoById_shouldReturnResponse_whenFound() {
        Todo todo = createTodo(1L, "Test", "Desc", false);
        given(todoRepository.findById(1L)).willReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test");
    }

    @Test
    @DisplayName("R07: getTodoById should throw TodoNotFoundException when not found")
    void getTodoById_shouldThrowException_whenNotFound() {
        given(todoRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodoById(999L))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    @DisplayName("R04: updateTodo should update and return TodoResponse")
    void updateTodo_shouldUpdateAndReturnResponse() {
        Todo existing = createTodo(1L, "Old title", "Old desc", false);
        given(todoRepository.findById(1L)).willReturn(Optional.of(existing));
        given(todoRepository.save(any(Todo.class))).willAnswer(inv -> inv.getArgument(0));

        TodoRequest request = new TodoRequest();
        request.setTitle("New title");
        request.setDescription("New desc");
        request.setCompleted(true);

        TodoResponse response = todoService.updateTodo(1L, request);

        assertThat(response.getTitle()).isEqualTo("New title");
        assertThat(response.getDescription()).isEqualTo("New desc");
        assertThat(response.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("R07: updateTodo should throw TodoNotFoundException when not found")
    void updateTodo_shouldThrowException_whenNotFound() {
        given(todoRepository.findById(999L)).willReturn(Optional.empty());

        TodoRequest request = new TodoRequest();
        request.setTitle("Title");

        assertThatThrownBy(() -> todoService.updateTodo(999L, request))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    @DisplayName("R05: deleteTodo should delete when found")
    void deleteTodo_shouldDelete_whenFound() {
        given(todoRepository.existsById(1L)).willReturn(true);

        todoService.deleteTodo(1L);

        then(todoRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("R07: deleteTodo should throw TodoNotFoundException when not found")
    void deleteTodo_shouldThrowException_whenNotFound() {
        given(todoRepository.existsById(999L)).willReturn(false);

        assertThatThrownBy(() -> todoService.deleteTodo(999L))
                .isInstanceOf(TodoNotFoundException.class);
        then(todoRepository).should(never()).deleteById(999L);
    }

    @Test
    @DisplayName("R13: toggleTodo should toggle completed status")
    void toggleTodo_shouldToggleCompletedStatus() {
        Todo todo = createTodo(1L, "Test", null, false);
        given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
        given(todoRepository.save(any(Todo.class))).willAnswer(inv -> inv.getArgument(0));

        TodoResponse response = todoService.toggleTodo(1L);

        assertThat(response.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("R13: toggleTodo should toggle from true to false")
    void toggleTodo_shouldToggleFromTrueToFalse() {
        Todo todo = createTodo(1L, "Test", null, true);
        given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
        given(todoRepository.save(any(Todo.class))).willAnswer(inv -> inv.getArgument(0));

        TodoResponse response = todoService.toggleTodo(1L);

        assertThat(response.getCompleted()).isFalse();
    }
}
