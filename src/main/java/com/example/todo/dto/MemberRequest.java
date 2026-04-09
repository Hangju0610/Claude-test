package com.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for registering a member")
public class MemberRequest {

    @Schema(description = "Username (unique)", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username must not be blank")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @Schema(description = "Email address (unique, valid format)", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
