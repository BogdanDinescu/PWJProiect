package com.bogdan.pharmacy.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String object) {
        super(object + " id not found");
    }
}