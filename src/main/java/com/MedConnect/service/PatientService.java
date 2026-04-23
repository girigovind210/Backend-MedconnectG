package com.MedConnect.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MedConnect.entity.Patient;
import com.MedConnect.repository.PatientRepository;
import org.springframework.http.ResponseEntity;

@org.springframework.context.annotation.Profile("!demo")
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Method to fetch patient details by ID
    public Patient getPatientById(Long id) {
    return patientRepository.findById(id)
            .orElse(null);   // 👈 return null instead of exception
}

    
}
