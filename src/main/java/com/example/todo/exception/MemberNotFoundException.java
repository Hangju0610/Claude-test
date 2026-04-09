package com.example.todo.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long id) {
        super("Member not found with id: " + id);
    }
}
