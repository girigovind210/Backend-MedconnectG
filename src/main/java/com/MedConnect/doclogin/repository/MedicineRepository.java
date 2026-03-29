package com.MedConnect.doclogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.MedConnect.doclogin.entity.Medicine;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Add this method to support case-insensitive search
    List<Medicine> findByDrugNameIgnoreCase(String drugName);
}
