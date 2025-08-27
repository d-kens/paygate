package com.example.paygate.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException() {
        super("Email already exist");
    }
}
