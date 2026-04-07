package com.example.todo.controller;

import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.DuplicateMemberException;
import com.example.todo.exception.MemberNotFoundException;
import com.example.todo.service.MemberService;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberWebController.class)
class MemberWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private TodoService todoService;

    private MemberResponse createMemberResponse(Long id, String username, String email) {
        MemberResponse response = new MemberResponse();
        response.setId(id);
        response.setUsername(username);
        response.setEmail(email);
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    // --- M12: Registration Page ---

    @Test
    @DisplayName("M12: GET /members/new should return registration form")
    void registrationForm_shouldReturnForm() throws Exception {
        mockMvc.perform(get("/members/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/form"));
    }

    @Test
    @DisplayName("M12: POST /members should register member and redirect to list")
    void registerMember_shouldRedirectToList() throws Exception {
        MemberResponse response = createMemberResponse(1L, "newuser", "new@example.com");
        given(memberService.registerMember(any())).willReturn(response);

        mockMvc.perform(post("/members")
                        .param("username", "newuser")
                        .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members"));
    }

    @Test
    @DisplayName("M12: POST /members should redisplay form on validation error")
    void registerMember_shouldRedisplayForm_onValidationError() throws Exception {
        mockMvc.perform(post("/members")
                        .param("username", "")
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("member/form"));
    }

    // --- M13: Member List Page ---

    @Test
    @DisplayName("M13: GET /members should show member list page")
    void memberList_shouldShowListPage() throws Exception {
        List<MemberResponse> members = List.of(
                createMemberResponse(1L, "user1", "u1@example.com")
        );
        given(memberService.getAllMembers()).willReturn(members);

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/list"))
                .andExpect(model().attributeExists("members"));
    }

    // --- M14: Member Todo List Page ---

    @Test
    @DisplayName("M14: GET /members/{id}/todos should show member's todo list page")
    void memberTodoList_shouldShowTodoListPage() throws Exception {
        MemberResponse member = createMemberResponse(1L, "testuser", "test@example.com");
        given(memberService.getMemberById(1L)).willReturn(member);

        TodoResponse todo = new TodoResponse();
        todo.setId(1L);
        todo.setTitle("Member's todo");
        todo.setCompleted(false);
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        given(memberService.getTodosByMemberId(1L)).willReturn(List.of(todo));

        mockMvc.perform(get("/members/1/todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/todos"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("todos"));
    }

    // --- M15: Toggle and Delete from member todo list ---

    @Test
    @DisplayName("M15: POST /members/{memberId}/todos/{todoId}/toggle should toggle and redirect")
    void toggleMemberTodo_shouldRedirect() throws Exception {
        mockMvc.perform(post("/members/1/todos/1/toggle"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/1/todos"));

        then(todoService).should().toggleTodo(1L);
    }

    @Test
    @DisplayName("M15: POST /members/{memberId}/todos/{todoId}/delete should delete and redirect")
    void deleteMemberTodo_shouldRedirect() throws Exception {
        mockMvc.perform(post("/members/1/todos/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/1/todos"));

        then(todoService).should().deleteTodo(1L);
    }

    // --- M09: Create todo for member from web UI ---

    @Test
    @DisplayName("M09: POST /members/{id}/todos should create todo and redirect")
    void createTodoForMember_shouldRedirect() throws Exception {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("New todo");
        todoResponse.setCreatedAt(LocalDateTime.now());
        todoResponse.setUpdatedAt(LocalDateTime.now());
        given(memberService.createTodoForMember(eq(1L), any())).willReturn(todoResponse);

        mockMvc.perform(post("/members/1/todos")
                        .param("title", "New todo")
                        .param("description", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/1/todos"));
    }
}
