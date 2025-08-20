package com.example.sapa.models;

public class UserProfileResponse {
    private String status;     // "success" or "error"
    private String message;    // Optional error message
    private UserData data;     // The user data payload

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }

    // Setters (required for JSON parsing)
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(UserData data) { this.data = data; }
}