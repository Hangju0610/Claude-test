package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());

        Todo saved = todoRepository.save(todo);
        return TodoResponse.from(saved);
    }

    public List<TodoResponse> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::from)
                .toList();
    }

    public TodoResponse getTodoById(Long id) {
        Todo todo = findTodoOrThrow(id);
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = findTodoOrThrow(id);

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }

        Todo saved = todoRepository.save(todo);
        return TodoResponse.from(saved);
    }

    @Transactional
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    @Transactional
    public TodoResponse toggleTodo(Long id) {
        Todo todo = findTodoOrThrow(id);
        todo.setCompleted(!todo.getCompleted());

        Todo saved = todoRepository.save(todo);
        return TodoResponse.from(saved);
    }

    private Todo findTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
