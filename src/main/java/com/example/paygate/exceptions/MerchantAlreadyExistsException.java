package com.example.paygate.exceptions;

public class MerchantAlreadyExistsException extends RuntimeException {
    public MerchantAlreadyExistsException() {
        super("User already has a merchant account");
    }
}
