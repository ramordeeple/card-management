package com.pm.bankcards.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;


public class BusinessException extends RuntimeException {

    private final ErrorCodes code;
    private final Map<String, Object> details;
    private final HttpStatus status;

    public BusinessException(ErrorCodes code, String message) {
        this(code, message, null, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(ErrorCodes code, String message, Map<String, Object> details) {
        this(code, message, details, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(ErrorCodes code, String message, Map<String, Object> details, HttpStatus status) {
        super(message);
        this.code = code;
        this.details = details;
        this.status = status;
    }

    public ErrorCodes getCode() {
        return code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
