package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse createTodo(TodoRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<TodoResponse> getAllTodos() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public TodoResponse getTodoById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteTodo(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public TodoResponse toggleTodo(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
