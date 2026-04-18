package com.MedConnect.repository;

import com.MedConnect.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // ✅ KEEP OLD METHOD (fix using query)
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId")
    List<Prescription> findByPatientId(Long patientId);

    // ✅ NEW METHOD FOR HISTORY
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId ORDER BY p.createdAt DESC")
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    
}