package com.fund.app.box.exception;

public class NonExistingCollectionBox extends RuntimeException {
    public NonExistingCollectionBox(String message) {
        super(message);
    }
}
