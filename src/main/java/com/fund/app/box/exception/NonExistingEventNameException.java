package com.fund.app.box.exception;

public class NonExistingEventNameException extends IllegalArgumentException {
    public NonExistingEventNameException(String message) {
        super(message);
    }
}
