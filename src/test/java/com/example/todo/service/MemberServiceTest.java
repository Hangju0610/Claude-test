package com.example.todo.service;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Member;
import com.example.todo.entity.Todo;
import com.example.todo.exception.DuplicateMemberException;
import com.example.todo.exception.MemberNotFoundException;
import com.example.todo.repository.MemberRepository;
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

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private MemberService memberService;

    private Member createMember(Long id, String username, String email) {
        Member member = new Member();
        member.setId(id);
        member.setUsername(username);
        member.setEmail(email);
        member.setCreatedAt(LocalDateTime.now());
        return member;
    }

    private Todo createTodo(Long id, String title, Member member) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setMember(member);
        todo.setCompleted(false);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        return todo;
    }

    // --- M01: Register Member ---

    @Test
    @DisplayName("M01: registerMember should save and return MemberResponse")
    void registerMember_shouldSaveAndReturnResponse() {
        MemberRequest request = new MemberRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");

        Member saved = createMember(1L, "newuser", "new@example.com");
        given(memberRepository.existsByUsername("newuser")).willReturn(false);
        given(memberRepository.existsByEmail("new@example.com")).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(saved);

        MemberResponse response = memberService.registerMember(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        then(memberRepository).should().save(any(Member.class));
    }

    // --- M04: Duplicate checks ---

    @Test
    @DisplayName("M04: registerMember should throw DuplicateMemberException for duplicate username")
    void registerMember_shouldThrowOnDuplicateUsername() {
        MemberRequest request = new MemberRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");

        given(memberRepository.existsByUsername("existing")).willReturn(true);

        assertThatThrownBy(() -> memberService.registerMember(request))
                .isInstanceOf(DuplicateMemberException.class);
    }

    @Test
    @DisplayName("M04: registerMember should throw DuplicateMemberException for duplicate email")
    void registerMember_shouldThrowOnDuplicateEmail() {
        MemberRequest request = new MemberRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");

        given(memberRepository.existsByUsername("newuser")).willReturn(false);
        given(memberRepository.existsByEmail("existing@example.com")).willReturn(true);

        assertThatThrownBy(() -> memberService.registerMember(request))
                .isInstanceOf(DuplicateMemberException.class);
    }

    // --- M05: Get Member by ID ---

    @Test
    @DisplayName("M05: getMemberById should return MemberResponse when found")
    void getMemberById_shouldReturnResponse_whenFound() {
        Member member = createMember(1L, "testuser", "test@example.com");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        MemberResponse response = memberService.getMemberById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    // --- M06: Member not found ---

    @Test
    @DisplayName("M06: getMemberById should throw MemberNotFoundException when not found")
    void getMemberById_shouldThrow_whenNotFound() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(999L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // --- M07: Get All Members ---

    @Test
    @DisplayName("M07: getAllMembers should return list of MemberResponse")
    void getAllMembers_shouldReturnAllMembers() {
        List<Member> members = List.of(
                createMember(1L, "user1", "u1@example.com"),
                createMember(2L, "user2", "u2@example.com")
        );
        given(memberRepository.findAll()).willReturn(members);

        List<MemberResponse> responses = memberService.getAllMembers();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUsername()).isEqualTo("user1");
        assertThat(responses.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("M07: getAllMembers should return empty list when no members")
    void getAllMembers_shouldReturnEmptyList_whenNoMembers() {
        given(memberRepository.findAll()).willReturn(List.of());

        List<MemberResponse> responses = memberService.getAllMembers();

        assertThat(responses).isEmpty();
    }

    // --- M08: Get Todos by Member ---

    @Test
    @DisplayName("M08: getTodosByMemberId should return todos for existing member")
    void getTodosByMemberId_shouldReturnTodos() {
        Member member = createMember(1L, "testuser", "test@example.com");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        List<Todo> todos = List.of(
                createTodo(1L, "Todo 1", member),
                createTodo(2L, "Todo 2", member)
        );
        given(todoRepository.findByMemberId(1L)).willReturn(todos);

        List<TodoResponse> responses = memberService.getTodosByMemberId(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Todo 1");
    }

    @Test
    @DisplayName("M08: getTodosByMemberId should return empty list for member with no todos")
    void getTodosByMemberId_shouldReturnEmpty_whenNoTodos() {
        Member member = createMember(1L, "testuser", "test@example.com");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(todoRepository.findByMemberId(1L)).willReturn(List.of());

        List<TodoResponse> responses = memberService.getTodosByMemberId(1L);

        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("M06: getTodosByMemberId should throw when member not found")
    void getTodosByMemberId_shouldThrow_whenMemberNotFound() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getTodosByMemberId(999L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // --- M09: Create Todo for Member ---

    @Test
    @DisplayName("M09: createTodoForMember should create todo with member association")
    void createTodoForMember_shouldCreateWithMemberAssociation() {
        Member member = createMember(1L, "testuser", "test@example.com");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        com.example.todo.dto.TodoRequest todoRequest = new com.example.todo.dto.TodoRequest();
        todoRequest.setTitle("Member's todo");
        todoRequest.setDescription("Description");

        Todo savedTodo = createTodo(1L, "Member's todo", member);
        savedTodo.setDescription("Description");
        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);

        TodoResponse response = memberService.createTodoForMember(1L, todoRequest);

        assertThat(response.getTitle()).isEqualTo("Member's todo");
        then(todoRepository).should().save(any(Todo.class));
    }

    @Test
    @DisplayName("M09: createTodoForMember should throw when member not found")
    void createTodoForMember_shouldThrow_whenMemberNotFound() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        com.example.todo.dto.TodoRequest todoRequest = new com.example.todo.dto.TodoRequest();
        todoRequest.setTitle("Orphan todo");

        assertThatThrownBy(() -> memberService.createTodoForMember(999L, todoRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
