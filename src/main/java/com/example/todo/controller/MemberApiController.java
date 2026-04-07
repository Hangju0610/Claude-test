package com.example.todo.controller;

import com.example.todo.dto.MemberRequest;
import com.example.todo.dto.MemberResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.ErrorResponse;
import com.example.todo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member", description = "Member registration and member-based TODO management API")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new member", description = "Creates a new member with unique username and email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MemberResponse registerMember(@Valid @RequestBody MemberRequest request) {
        return memberService.registerMember(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a member by ID", description = "Returns a single member by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MemberResponse getMemberById(@Parameter(description = "Member ID") @PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @GetMapping
    @Operation(summary = "Get all members", description = "Returns a list of all registered members")
    @ApiResponse(responseCode = "200", description = "List of members retrieved successfully")
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{memberId}/todos")
    @Operation(summary = "Get todos by member", description = "Returns all TODO items belonging to the specified member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of member's TODOs retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<TodoResponse> getTodosByMember(@Parameter(description = "Member ID") @PathVariable Long memberId) {
        return memberService.getTodosByMemberId(memberId);
    }

    @PostMapping("/{memberId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a TODO for a member", description = "Creates a new TODO item associated with the specified member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TODO created successfully for member"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TodoResponse createTodoForMember(@Parameter(description = "Member ID") @PathVariable Long memberId,
                                             @Valid @RequestBody TodoRequest request) {
        return memberService.createTodoForMember(memberId, request);
    }
}
