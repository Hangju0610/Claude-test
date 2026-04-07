package com.example.todo.controller;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse registerMember(@Valid @RequestBody MemberRequest request) {
        return memberService.registerMember(request);
    }

    @GetMapping("/{id}")
    public MemberResponse getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{memberId}/todos")
    public List<TodoResponse> getTodosByMember(@PathVariable Long memberId) {
        return memberService.getTodosByMemberId(memberId);
    }

    @PostMapping("/{memberId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodoForMember(@PathVariable Long memberId,
                                             @Valid @RequestBody TodoRequest request) {
        return memberService.createTodoForMember(memberId, request);
    }
}
