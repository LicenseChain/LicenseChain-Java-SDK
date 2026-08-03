package com.licensechain.exceptions;

public class NetworkException extends LicenseChainException {
    public NetworkException(String message) {
        super(message, "NETWORK_ERROR");
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, "NETWORK_ERROR", cause);
    }
}
