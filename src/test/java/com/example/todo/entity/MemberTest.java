package com.example.todo.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("M01: Member entity should hold username and email")
    void memberEntity_shouldHoldUsernameAndEmail() {
        Member member = new Member();
        member.setUsername("testuser");
        member.setEmail("test@example.com");

        assertThat(member.getUsername()).isEqualTo("testuser");
        assertThat(member.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("M02: Username must not be blank")
    void username_shouldNotBeBlank() {
        Member member = new Member();
        member.setUsername("");
        member.setEmail("test@example.com");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    @DisplayName("M02: Username must not exceed 50 characters")
    void username_shouldNotExceed50Characters() {
        Member member = new Member();
        member.setUsername("a".repeat(51));
        member.setEmail("test@example.com");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    @DisplayName("M03: Email must not be blank")
    void email_shouldNotBeBlank() {
        Member member = new Member();
        member.setUsername("testuser");
        member.setEmail("");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("M03: Email must not exceed 100 characters")
    void email_shouldNotExceed100Characters() {
        Member member = new Member();
        member.setUsername("testuser");
        member.setEmail("a".repeat(90) + "@example.com");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("M03: Email must have valid format")
    void email_shouldHaveValidFormat() {
        Member member = new Member();
        member.setUsername("testuser");
        member.setEmail("not-an-email");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("M01: Valid member should have no validation errors")
    void validMember_shouldHaveNoViolations() {
        Member member = new Member();
        member.setUsername("testuser");
        member.setEmail("test@example.com");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("M10: Todo entity should have nullable member field")
    void todo_shouldHaveNullableMemberField() {
        Todo todo = new Todo();
        todo.setTitle("Test todo");
        assertThat(todo.getMember()).isNull();
    }

    @Test
    @DisplayName("M10: Todo entity should accept member association")
    void todo_shouldAcceptMemberAssociation() {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("testuser");

        Todo todo = new Todo();
        todo.setTitle("Test todo");
        todo.setMember(member);

        assertThat(todo.getMember()).isNotNull();
        assertThat(todo.getMember().getId()).isEqualTo(1L);
    }
}
