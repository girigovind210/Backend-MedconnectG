package com.MedConnect.repository;

import com.MedConnect.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // ✅ CORRECT METHOD (NO QUERY NEEDED)
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);

}