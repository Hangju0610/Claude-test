package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.ErrorResponse;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "TODO", description = "TODO List CRUD API")
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new TODO", description = "Creates a new TODO item with title and optional description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TODO created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TodoResponse createTodo(@Valid @RequestBody TodoRequest request) {
        return todoService.createTodo(request);
    }

    @GetMapping
    @Operation(summary = "Get all TODOs", description = "Returns a list of all TODO items")
    @ApiResponse(responseCode = "200", description = "List of TODOs retrieved successfully")
    public List<TodoResponse> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a TODO by ID", description = "Returns a single TODO item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO found"),
            @ApiResponse(responseCode = "404", description = "TODO not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TodoResponse getTodoById(@Parameter(description = "TODO ID") @PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a TODO", description = "Updates an existing TODO item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "TODO not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TodoResponse updateTodo(@Parameter(description = "TODO ID") @PathVariable Long id,
                                   @Valid @RequestBody TodoRequest request) {
        return todoService.updateTodo(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a TODO", description = "Deletes a TODO item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "TODO deleted successfully"),
            @ApiResponse(responseCode = "404", description = "TODO not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void deleteTodo(@Parameter(description = "TODO ID") @PathVariable Long id) {
        todoService.deleteTodo(id);
    }
}
