package com.MedConnect.entity;

import jakarta.persistence.*;
import com.MedConnect.doclogin.entity.Medicine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 Avoid circular JSON
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    private Patient patient;

    // ✅ Medicine mapping
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Medicine medicine;

    private String dosage;

    // 🔥 Store JSON string in DB
    @JsonIgnore
    @Column(name = "time_to_take", columnDefinition = "JSON")
    private String timeToTake;

            @Column(name = "created_at")
        private LocalDateTime createdAt;

    // ✅ Send as List to frontend (IMPORTANT FIX)
      @JsonProperty("timeToTake")
public List<String> getTimeToTake() {
    try {
        if (this.timeToTake == null || this.timeToTake.isEmpty()) {
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(this.timeToTake, new TypeReference<List<String>>() {});
    } catch (Exception e) {
        // fallback (NO CRASH)
        List<String> list = new ArrayList<>();
        list.add(this.timeToTake);
        return list;
    }
}

    // ✅ Setter (used while saving)
    public void setTimeToTake(String timeToTake) {
        this.timeToTake = timeToTake;
    }

    // 🔧 Constructors
    public Prescription() {}

    public Prescription(Patient patient, Medicine medicine, String dosage, String timeToTake) {
        this.patient = patient;
        this.medicine = medicine;
        this.dosage = dosage;
        this.timeToTake = timeToTake;
    }

    // 🔧 Getters & Setters

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getTimeToTakeRaw() 
    {
    return timeToTake;
    }

    public LocalDateTime getCreatedAt() {
    return createdAt;
    }

        public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    }
}