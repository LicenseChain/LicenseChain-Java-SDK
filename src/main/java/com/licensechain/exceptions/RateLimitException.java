package com.licensechain.exceptions;

import java.util.Map;

public class RateLimitException extends LicenseChainException {
    public RateLimitException(String message) {
        super(message, "RATE_LIMIT_ERROR");
    }
    
    public RateLimitException(String message, Throwable cause) {
        super(message, "RATE_LIMIT_ERROR", cause);
    }

    public RateLimitException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message, code, statusCode, details);
    }
}
