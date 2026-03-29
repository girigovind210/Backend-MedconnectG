package com.MedConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.MedConnect.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long>
{
	@Query("SELECT p FROM Patient p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR str(p.id) LIKE CONCAT('%', :query, '%')")
	List<Patient> searchByNameOrId(@Param("query") String query);

}
