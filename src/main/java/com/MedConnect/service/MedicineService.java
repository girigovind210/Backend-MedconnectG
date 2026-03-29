package com.MedConnect.service;

import com.MedConnect.doclogin.entity.Medicine;
import com.MedConnect.doclogin.repository.MedicineRepository;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@org.springframework.context.annotation.Profile("!demo")
@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // Fetch medicine by its name (or any other identifier you prefer)
    public Medicine getMedicineByName(String name) {
        List<Medicine> medicines = medicineRepository.findByDrugNameIgnoreCase(name);
        if (!medicines.isEmpty()) {
            return medicines.get(0); // Use the first one for now (or apply logic to pick one)
        }
        return null;
    }




    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

}
