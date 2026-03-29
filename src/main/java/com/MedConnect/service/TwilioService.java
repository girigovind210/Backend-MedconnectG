package com.MedConnect.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.context.annotation.Profile("!demo")
@Service
public class TwilioService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Value("${TWILIO_ACCOUNT_SID}")
    private String accountSid;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String authToken;

    @Value("${TWILIO_WHATSAPP_FROM}")
    private String whatsappFrom;  // e.g. whatsapp:+14155238886

    @PostConstruct
    public void init() {
        logger.info("✅ Twilio initialized with SID: {}", accountSid);
        logger.info("📲 Twilio From: {}", whatsappFrom);
        Twilio.init(accountSid, authToken);
    }

    public void sendWhatsAppMessageWithMedia(String toPhoneNumber, String mediaUrl, String caption) {
        try {
            // Normalize the phone number to ensure it starts with +91
            if (!toPhoneNumber.startsWith("+")) {
                toPhoneNumber = "+91" + toPhoneNumber.trim();
            }

            logger.info("Sending WhatsApp message to: {}", toPhoneNumber);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + toPhoneNumber),
                    new PhoneNumber(whatsappFrom),
                    caption
            ).setMediaUrl(List.of(URI.create(mediaUrl)))
             .create();

            logger.info("✅ Message sent with SID: {}", message.getSid());
        } catch (Exception e) {
            logger.error("❌ Error sending WhatsApp message: {}", e.getMessage());
        }
    }

}
