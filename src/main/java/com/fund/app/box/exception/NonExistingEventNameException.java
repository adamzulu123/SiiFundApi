package com.fund.app.box.exception;

public class NonExistingEventNameException extends RuntimeException {
    public NonExistingEventNameException(String message) {
        super(message);
    }
}
