package com.licensechain.exceptions;

import java.util.Map;

public class ServerException extends LicenseChainException {
    public ServerException(String message) {
        super(message, "SERVER_ERROR");
    }
    
    public ServerException(String message, Throwable cause) {
        super(message, "SERVER_ERROR", cause);
    }

    public ServerException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message, code, statusCode, details);
    }
}
