package com.MedConnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MedConnect.service.TwilioService;
@CrossOrigin(origins = "https://medconnect-frontend-1.onrender.com")
@org.springframework.context.annotation.Profile("!demo")
@RestController
@RequestMapping("/api/patients")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;  // Injecting TwilioService

    // Endpoint to send prescription via WhatsApp
    @PostMapping("/send-prescription")
    public ResponseEntity<String> sendPrescription(@RequestBody PrescriptionRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required");
        }

        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body("PDF URL is required");
        }

        try {
            // message = PDF URL, phoneNumber = patient number
            twilioService.sendWhatsAppMessageWithMedia(request.getPhoneNumber(), request.getMessage(), "Here is your prescription PDF");
            return ResponseEntity.ok("Prescription PDF sent via WhatsApp!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send prescription PDF: " + e.getMessage());
        }
    }

}

// DTO class to hold the request data (phone number and message)
class PrescriptionRequest {
    private String phoneNumber;
    private String message;

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
