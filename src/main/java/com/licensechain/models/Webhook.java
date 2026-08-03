package com.licensechain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class Webhook {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("events")
    private List<String> events;
    
    @JsonProperty("secret")
    private String secret;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Webhook() {}
    
    public Webhook(String id, String url, List<String> events, String secret,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.url = url;
        this.events = events;
        this.secret = secret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public List<String> getEvents() {
        return events;
    }
    
    public void setEvents(List<String> events) {
        this.events = events;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
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
    
    @Override
    public String toString() {
        return "Webhook{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", events=" + events +
                ", secret='" + secret + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
