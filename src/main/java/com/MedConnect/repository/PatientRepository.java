package com.MedConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.MedConnect.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

   @Query("SELECT p FROM Patient p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR str(p.id) LIKE CONCAT('%', :query, '%') OR p.phoneNumber LIKE CONCAT('%', :query, '%')")
List<Patient> searchByNameOrId(@Param("query") String query);

    // ❌ REMOVE custom findById
}