package com.licensechain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

public class License {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("product_id")
    private String productId;
    
    @JsonProperty("license_key")
    private String licenseKey;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    // Constructors
    public License() {}
    
    public License(String id, String userId, String productId, String licenseKey, String status,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime expiresAt,
                   Map<String, Object> metadata) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.licenseKey = licenseKey;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.metadata = metadata;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getLicenseKey() {
        return licenseKey;
    }
    
    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "License{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", licenseKey='" + licenseKey + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", expiresAt=" + expiresAt +
                ", metadata=" + metadata +
                '}';
    }
}
