package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoApiController.class)
class TodoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    // === CREATE ===

    @Test
    @DisplayName("R01: POST /api/todos should create todo and return 201")
    void createTodo_shouldReturn201_withCreatedTodo() throws Exception {
        TodoResponse response = createResponse(1L, "New todo", "Description", false);
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(response);

        TodoRequest request = new TodoRequest();
        request.setTitle("New todo");
        request.setDescription("Description");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New todo"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("R08: POST /api/todos with blank title should return 400")
    void createTodo_withBlankTitle_shouldReturn400() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("R08: POST /api/todos with null title should return 400")
    void createTodo_withNullTitle_shouldReturn400() throws Exception {
        TodoRequest request = new TodoRequest();

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("R06: POST /api/todos with title > 200 chars should return 400")
    void createTodo_withTitleExceeding200Chars_shouldReturn400() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("a".repeat(201));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // === READ ALL ===

    @Test
    @DisplayName("R02: GET /api/todos should return all todos")
    void getAllTodos_shouldReturnAllTodos() throws Exception {
        List<TodoResponse> responses = List.of(
                createResponse(1L, "Todo 1", null, false),
                createResponse(2L, "Todo 2", "Desc", true)
        );
        given(todoService.getAllTodos()).willReturn(responses);

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Todo 1"))
                .andExpect(jsonPath("$[1].title").value("Todo 2"));
    }

    @Test
    @DisplayName("R02: GET /api/todos should return empty array when no todos")
    void getAllTodos_shouldReturnEmptyArray_whenNoTodos() throws Exception {
        given(todoService.getAllTodos()).willReturn(List.of());

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // === READ ONE ===

    @Test
    @DisplayName("R03: GET /api/todos/{id} should return todo when found")
    void getTodoById_shouldReturnTodo_whenFound() throws Exception {
        TodoResponse response = createResponse(1L, "Test", "Desc", false);
        given(todoService.getTodoById(1L)).willReturn(response);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    @DisplayName("R07: GET /api/todos/{id} should return 404 when not found")
    void getTodoById_shouldReturn404_whenNotFound() throws Exception {
        given(todoService.getTodoById(999L)).willThrow(new TodoNotFoundException(999L));

        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound());
    }

    // === UPDATE ===

    @Test
    @DisplayName("R04: PUT /api/todos/{id} should update and return todo")
    void updateTodo_shouldReturnUpdatedTodo() throws Exception {
        TodoResponse response = createResponse(1L, "Updated", "New desc", true);
        given(todoService.updateTodo(eq(1L), any(TodoRequest.class))).willReturn(response);

        TodoRequest request = new TodoRequest();
        request.setTitle("Updated");
        request.setDescription("New desc");
        request.setCompleted(true);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("R07: PUT /api/todos/{id} should return 404 when not found")
    void updateTodo_shouldReturn404_whenNotFound() throws Exception {
        given(todoService.updateTodo(eq(999L), any(TodoRequest.class)))
                .willThrow(new TodoNotFoundException(999L));

        TodoRequest request = new TodoRequest();
        request.setTitle("Title");

        mockMvc.perform(put("/api/todos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("R08: PUT /api/todos/{id} with blank title should return 400")
    void updateTodo_withBlankTitle_shouldReturn400() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("");

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // === DELETE ===

    @Test
    @DisplayName("R05: DELETE /api/todos/{id} should return 204")
    void deleteTodo_shouldReturn204() throws Exception {
        willDoNothing().given(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("R07: DELETE /api/todos/{id} should return 404 when not found")
    void deleteTodo_shouldReturn404_whenNotFound() throws Exception {
        willThrow(new TodoNotFoundException(999L)).given(todoService).deleteTodo(999L);

        mockMvc.perform(delete("/api/todos/999"))
                .andExpect(status().isNotFound());
    }

    // === ERROR RESPONSE FORMAT ===

    @Test
    @DisplayName("R14: 404 error should return structured JSON error response")
    void notFoundError_shouldReturnStructuredErrorResponse() throws Exception {
        given(todoService.getTodoById(999L)).willThrow(new TodoNotFoundException(999L));

        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("R14: Validation error should return structured JSON error response")
    void validationError_shouldReturnStructuredErrorResponse() throws Exception {
        TodoRequest request = new TodoRequest();
        request.setTitle("");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
