package com.licensechain.exceptions;

import java.util.Map;

public class NotFoundException extends LicenseChainException {
    public NotFoundException(String message) {
        super(message, "NOT_FOUND_ERROR");
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, "NOT_FOUND_ERROR", cause);
    }

    public NotFoundException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message, code, statusCode, details);
    }
}
