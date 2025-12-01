package com.project.shopapp.shared.exceptions;

public class PermissionDenyException extends RuntimeException {
    public PermissionDenyException(String message) {
        super(message);
    }
}
