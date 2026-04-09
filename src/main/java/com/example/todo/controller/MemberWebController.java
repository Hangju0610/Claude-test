package com.example.todo.controller;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.TodoRequest;
import com.example.todo.service.MemberService;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class MemberWebController {

    private final MemberService memberService;
    private final TodoService todoService;

    public MemberWebController(MemberService memberService, TodoService todoService) {
        this.memberService = memberService;
        this.todoService = todoService;
    }

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", memberService.getAllMembers());
        return "member/list";
    }

    @GetMapping("/new")
    public String registrationForm(Model model) {
        model.addAttribute("memberRequest", new MemberRequest());
        return "member/form";
    }

    @PostMapping
    public String registerMember(@Valid @ModelAttribute MemberRequest memberRequest,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/form";
        }
        memberService.registerMember(memberRequest);
        return "redirect:/members";
    }

    @GetMapping("/{memberId}/todos")
    public String memberTodoList(@PathVariable Long memberId, Model model) {
        model.addAttribute("member", memberService.getMemberById(memberId));
        model.addAttribute("todos", memberService.getTodosByMemberId(memberId));
        model.addAttribute("todoRequest", new TodoRequest());
        return "member/todos";
    }

    @PostMapping("/{memberId}/todos")
    public String createTodoForMember(@PathVariable Long memberId,
                                       @Valid @ModelAttribute TodoRequest todoRequest,
                                       BindingResult bindingResult,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", memberService.getMemberById(memberId));
            model.addAttribute("todos", memberService.getTodosByMemberId(memberId));
            return "member/todos";
        }
        memberService.createTodoForMember(memberId, todoRequest);
        return "redirect:/members/" + memberId + "/todos";
    }

    @PostMapping("/{memberId}/todos/{todoId}/toggle")
    public String toggleMemberTodo(@PathVariable Long memberId, @PathVariable Long todoId) {
        todoService.toggleTodo(todoId);
        return "redirect:/members/" + memberId + "/todos";
    }

    @PostMapping("/{memberId}/todos/{todoId}/delete")
    public String deleteMemberTodo(@PathVariable Long memberId, @PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return "redirect:/members/" + memberId + "/todos";
    }
}
