package com.MedConnect.entity;

import jakarta.persistence.*;
import com.MedConnect.doclogin.entity.Medicine;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;



    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Medicine medicine;

    private String dosage;

    @JsonIgnore // hide raw JSON string from frontend
    @Column(name = "time_to_take", columnDefinition = "JSON")
    private String timeToTake;

    // Send this as an array to the frontend
    @JsonProperty("timeToTake")
    public List<String> getTimeToTakeList() {
        if (this.timeToTake == null) return new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(this.timeToTake, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getTimeToTake() {
        return timeToTake;
    }

    // Keep this for saving JSON string
    public void setTimeToTake(String timeToTake) {
        this.timeToTake = timeToTake;
    }

    // Constructors
    public Prescription() {}

    public Prescription(Patient patient, Medicine medicine, String dosage, String timeToTake) {
        this.patient = patient;
        this.medicine = medicine;
        this.dosage = dosage;
        this.timeToTake = timeToTake;
    }

    // Other getters and setters
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
}
