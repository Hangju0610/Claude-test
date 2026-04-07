package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/todos")
public class TodoWebController {

    private final TodoService todoService;

    public TodoWebController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String listTodos(Model model) {
        model.addAttribute("todos", todoService.getAllTodos());
        return "todo/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("todoRequest", new TodoRequest());
        return "todo/form";
    }

    @PostMapping
    public String createTodo(@Valid @ModelAttribute TodoRequest todoRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "todo/form";
        }
        todoService.createTodo(todoRequest);
        return "redirect:/todos";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        TodoResponse todo = todoService.getTodoById(id);
        TodoRequest request = new TodoRequest();
        request.setTitle(todo.getTitle());
        request.setDescription(todo.getDescription());
        request.setCompleted(todo.getCompleted());
        model.addAttribute("todoRequest", request);
        model.addAttribute("todoId", id);
        return "todo/form";
    }

    @PostMapping("/{id}")
    public String updateTodo(@PathVariable Long id,
                             @Valid @ModelAttribute TodoRequest todoRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("todoId", id);
            return "todo/form";
        }
        todoService.updateTodo(id, todoRequest);
        return "redirect:/todos";
    }

    @PostMapping("/{id}/delete")
    public String deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return "redirect:/todos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleTodo(@PathVariable Long id) {
        todoService.toggleTodo(id);
        return "redirect:/todos";
    }
}
