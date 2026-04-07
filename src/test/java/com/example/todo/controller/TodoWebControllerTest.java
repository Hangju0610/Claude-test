package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoWebController.class)
class TodoWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    private TodoResponse createResponse(Long id, String title, String description, boolean completed) {
        TodoResponse response = new TodoResponse();
        response.setId(id);
        response.setTitle(title);
        response.setDescription(description);
        response.setCompleted(completed);
        response.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        response.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        return response;
    }

    @Test
    @DisplayName("R11: GET /todos should display todo list page")
    void listTodos_shouldDisplayListPage() throws Exception {
        List<TodoResponse> todos = List.of(
                createResponse(1L, "Todo 1", null, false),
                createResponse(2L, "Todo 2", "Desc", true)
        );
        given(todoService.getAllTodos()).willReturn(todos);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/list"))
                .andExpect(model().attributeExists("todos"));
    }

    @Test
    @DisplayName("R12: GET /todos/new should display create form")
    void createForm_shouldDisplayCreateForm() throws Exception {
        mockMvc.perform(get("/todos/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/form"))
                .andExpect(model().attributeExists("todoRequest"));
    }

    @Test
    @DisplayName("R12: POST /todos should create todo and redirect to list")
    void createTodo_shouldCreateAndRedirect() throws Exception {
        TodoResponse response = createResponse(1L, "New todo", "Desc", false);
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(response);

        mockMvc.perform(post("/todos")
                        .param("title", "New todo")
                        .param("description", "Desc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));
    }

    @Test
    @DisplayName("R12: POST /todos with invalid data should return form with errors")
    void createTodo_withInvalidData_shouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/todos")
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/form"));
    }

    @Test
    @DisplayName("R12: GET /todos/{id}/edit should display edit form")
    void editForm_shouldDisplayEditForm() throws Exception {
        TodoResponse response = createResponse(1L, "Test", "Desc", false);
        given(todoService.getTodoById(1L)).willReturn(response);

        mockMvc.perform(get("/todos/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/form"))
                .andExpect(model().attributeExists("todoRequest"))
                .andExpect(model().attribute("todoId", 1L));
    }

    @Test
    @DisplayName("R04: POST /todos/{id} should update todo and redirect")
    void updateTodo_shouldUpdateAndRedirect() throws Exception {
        TodoResponse response = createResponse(1L, "Updated", "New desc", true);
        given(todoService.updateTodo(eq(1L), any(TodoRequest.class))).willReturn(response);

        mockMvc.perform(post("/todos/1")
                        .param("title", "Updated")
                        .param("description", "New desc")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));
    }

    @Test
    @DisplayName("R05: POST /todos/{id}/delete should delete and redirect")
    void deleteTodo_shouldDeleteAndRedirect() throws Exception {
        mockMvc.perform(post("/todos/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        then(todoService).should().deleteTodo(1L);
    }

    @Test
    @DisplayName("R13: POST /todos/{id}/toggle should toggle and redirect")
    void toggleTodo_shouldToggleAndRedirect() throws Exception {
        TodoResponse response = createResponse(1L, "Test", null, true);
        given(todoService.toggleTodo(1L)).willReturn(response);

        mockMvc.perform(post("/todos/1/toggle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));

        then(todoService).should().toggleTodo(1L);
    }
}
