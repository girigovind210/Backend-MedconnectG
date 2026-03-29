package com.MedConnect.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MedConnect.entity.Patient;
import com.MedConnect.repository.PatientRepository;

@org.springframework.context.annotation.Profile("!demo")
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Method to fetch patient details by ID
    public Patient getPatientById(Long id) {
        // Assuming you're using JPA or a similar ORM for fetching data
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id " + id));
    }
}
