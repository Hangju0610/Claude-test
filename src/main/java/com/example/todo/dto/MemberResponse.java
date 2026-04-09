package com.example.todo.dto;

import com.example.todo.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body representing a member")
public class MemberResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setUsername(member.getUsername());
        response.setEmail(member.getEmail());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
