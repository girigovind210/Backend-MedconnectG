package com.MedConnect.repository;

import com.MedConnect.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
	@Query("SELECT p FROM Patient p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR str(p.id) LIKE CONCAT('%', :query, '%')")
	List<Prescription> findByPatientId(Long patientId);


}
