package com.licensechain.exceptions;

import java.util.Map;

public class AuthenticationException extends LicenseChainException {
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR");
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_ERROR", cause);
    }

    public AuthenticationException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message, code, statusCode, details);
    }
}
