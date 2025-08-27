package com.example.paygate.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Old password does not match");
    }
}
