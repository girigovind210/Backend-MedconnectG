package com.MedConnect.dto;

public class PrescriptionRequest {

    private String phoneNumber;
    private String mediaUrl; // URL to the prescription PDF
    private String message;  // The message to send along with the prescription

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
