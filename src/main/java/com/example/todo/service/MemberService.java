package com.example.todo.service;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Member;
import com.example.todo.entity.Todo;
import com.example.todo.exception.DuplicateMemberException;
import com.example.todo.exception.MemberNotFoundException;
import com.example.todo.repository.MemberRepository;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    public MemberService(MemberRepository memberRepository, TodoRepository todoRepository) {
        this.memberRepository = memberRepository;
        this.todoRepository = todoRepository;
    }

    @Transactional
    public MemberResponse registerMember(MemberRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateMemberException("Username already exists: " + request.getUsername());
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateMemberException("Email already exists: " + request.getEmail());
        }

        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setEmail(request.getEmail());

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    public MemberResponse getMemberById(Long id) {
        Member member = findMemberOrThrow(id);
        return toResponse(member);
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TodoResponse> getTodosByMemberId(Long memberId) {
        findMemberOrThrow(memberId);
        return todoRepository.findByMemberId(memberId).stream()
                .map(this::toTodoResponse)
                .toList();
    }

    @Transactional
    public TodoResponse createTodoForMember(Long memberId, TodoRequest request) {
        Member member = findMemberOrThrow(memberId);

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setMember(member);

        Todo saved = todoRepository.save(todo);
        return toTodoResponse(saved);
    }

    private Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    private MemberResponse toResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setUsername(member.getUsername());
        response.setEmail(member.getEmail());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }

    private TodoResponse toTodoResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.getCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}
