package com.arnautou.pictrip.web.rest.errors;

import org.springframework.http.HttpStatus;

public class ErrorDetails {

    private HttpStatus status;
    private String errorKey;
    private String errorMessage;

    public ErrorDetails(HttpStatus status, String errorKey, String errorMessage) {
        this.status = status;
        this.errorKey = errorKey;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
