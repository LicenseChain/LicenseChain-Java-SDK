package com.licensechain.exceptions;

import java.util.Map;

public class ValidationException extends LicenseChainException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }

    public ValidationException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message, code, statusCode, details);
    }
}
