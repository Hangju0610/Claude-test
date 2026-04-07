package com.example.todo.controller;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.DuplicateMemberException;
import com.example.todo.exception.MemberNotFoundException;
import com.example.todo.service.MemberService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MemberResponse createMemberResponse(Long id, String username, String email) {
        MemberResponse response = new MemberResponse();
        response.setId(id);
        response.setUsername(username);
        response.setEmail(email);
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    // --- M01: Register Member ---

    @Test
    @DisplayName("M01: POST /api/members should create member and return 201")
    void createMember_shouldReturn201() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");

        MemberResponse response = createMemberResponse(1L, "newuser", "new@example.com");
        given(memberService.registerMember(any(MemberRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    // --- M02/M03: Validation ---

    @Test
    @DisplayName("M02: POST /api/members should return 400 for blank username")
    void createMember_shouldReturn400_forBlankUsername() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setUsername("");
        request.setEmail("valid@example.com");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("M03: POST /api/members should return 400 for invalid email")
    void createMember_shouldReturn400_forInvalidEmail() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setUsername("validuser");
        request.setEmail("not-an-email");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("M03: POST /api/members should return 400 for blank email")
    void createMember_shouldReturn400_forBlankEmail() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setUsername("validuser");
        request.setEmail("");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- M04: Duplicate ---

    @Test
    @DisplayName("M04: POST /api/members should return 409 for duplicate username")
    void createMember_shouldReturn409_forDuplicateUsername() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");

        given(memberService.registerMember(any(MemberRequest.class)))
                .willThrow(new DuplicateMemberException("Username already exists"));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- M05: Get Member by ID ---

    @Test
    @DisplayName("M05: GET /api/members/{id} should return member")
    void getMember_shouldReturnMember() throws Exception {
        MemberResponse response = createMemberResponse(1L, "testuser", "test@example.com");
        given(memberService.getMemberById(1L)).willReturn(response);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // --- M06: Member not found ---

    @Test
    @DisplayName("M06: GET /api/members/{id} should return 404 for non-existent member")
    void getMember_shouldReturn404_whenNotFound() throws Exception {
        given(memberService.getMemberById(999L)).willThrow(new MemberNotFoundException(999L));

        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());
    }

    // --- M07: Get All Members ---

    @Test
    @DisplayName("M07: GET /api/members should return all members")
    void getAllMembers_shouldReturnAll() throws Exception {
        List<MemberResponse> members = List.of(
                createMemberResponse(1L, "user1", "u1@example.com"),
                createMemberResponse(2L, "user2", "u2@example.com")
        );
        given(memberService.getAllMembers()).willReturn(members);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    // --- M08: Get Todos by Member ---

    @Test
    @DisplayName("M08: GET /api/members/{id}/todos should return member's todos")
    void getTodosByMember_shouldReturnTodos() throws Exception {
        TodoResponse todo = new TodoResponse();
        todo.setId(1L);
        todo.setTitle("Member's todo");
        todo.setCompleted(false);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        given(memberService.getTodosByMemberId(1L)).willReturn(List.of(todo));

        mockMvc.perform(get("/api/members/1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Member's todo"));
    }

    @Test
    @DisplayName("M06: GET /api/members/{id}/todos should return 404 for non-existent member")
    void getTodosByMember_shouldReturn404_whenMemberNotFound() throws Exception {
        given(memberService.getTodosByMemberId(999L)).willThrow(new MemberNotFoundException(999L));

        mockMvc.perform(get("/api/members/999/todos"))
                .andExpect(status().isNotFound());
    }

    // --- M09: Create Todo for Member ---

    @Test
    @DisplayName("M09: POST /api/members/{id}/todos should create todo and return 201")
    void createTodoForMember_shouldReturn201() throws Exception {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("New member todo");
        todoRequest.setDescription("Description");

        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("New member todo");
        todoResponse.setDescription("Description");
        todoResponse.setCompleted(false);
        todoResponse.setCreatedAt(LocalDateTime.now());
        todoResponse.setUpdatedAt(LocalDateTime.now());

        given(memberService.createTodoForMember(eq(1L), any(TodoRequest.class))).willReturn(todoResponse);

        mockMvc.perform(post("/api/members/1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New member todo"));
    }

    @Test
    @DisplayName("M09: POST /api/members/{id}/todos should return 404 for non-existent member")
    void createTodoForMember_shouldReturn404_whenMemberNotFound() throws Exception {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("Orphan todo");

        given(memberService.createTodoForMember(eq(999L), any(TodoRequest.class)))
                .willThrow(new MemberNotFoundException(999L));

        mockMvc.perform(post("/api/members/999/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isNotFound());
    }
}
