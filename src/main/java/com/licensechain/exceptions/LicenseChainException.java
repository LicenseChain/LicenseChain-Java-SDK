package com.licensechain.exceptions;

import java.util.Map;

public class LicenseChainException extends Exception {
    private final String code;
    private final int statusCode;
    private final Map<String, Object> details;
    
    public LicenseChainException(String message) {
        super(message);
        this.code = "LICENSECHAIN_ERROR";
        this.statusCode = 0;
        this.details = null;
    }
    
    public LicenseChainException(String message, String code) {
        super(message);
        this.code = code;
        this.statusCode = 0;
        this.details = null;
    }
    
    public LicenseChainException(String message, Throwable cause) {
        super(message, cause);
        this.code = "LICENSECHAIN_ERROR";
        this.statusCode = 0;
        this.details = null;
    }
    
    public LicenseChainException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.statusCode = 0;
        this.details = null;
    }

    public LicenseChainException(String message, String code, int statusCode, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
        this.details = details;
    }
    
    public String getCode() {
        return code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
