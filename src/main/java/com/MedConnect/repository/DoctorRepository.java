package com.MedConnect.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.MedConnect.doclogin.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}