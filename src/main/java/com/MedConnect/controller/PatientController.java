package com.MedConnect.controller;

import com.MedConnect.entity.Patient;
import com.MedConnect.entity.Prescription;
import com.MedConnect.repository.PatientRepository;
import com.MedConnect.service.MedicineService;
import com.MedConnect.service.TwilioService;
import com.MedConnect.doclogin.entity.Medicine;
import com.MedConnect.model.MedicineWithTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/patients")
@CrossOrigin(origins = "https://medconnect-frontend-1.onrender.com")
public class PatientController {

    private final PatientRepository patientRepository;
    private final TwilioService twilioService;
    private final MedicineService medicineService;

    public PatientController(PatientRepository patientRepository,
                             TwilioService twilioService,
                             MedicineService medicineService) {
        this.patientRepository = patientRepository;
        this.twilioService = twilioService;
        this.medicineService = medicineService;
    }

    // =========================
    // GET ALL
    // =========================
        @GetMapping(produces = "application/json")
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            return new ResponseEntity<>(patientRepository.save(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id,
                                                 @RequestBody Patient updatedPatient) {
        try {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            patient.setName(updatedPatient.getName());
            patient.setAge(updatedPatient.getAge());
            patient.setBlood(updatedPatient.getBlood());
            patient.setPhoneNumber(updatedPatient.getPhoneNumber());
            patient.setDose(updatedPatient.getDose());
            patient.setFees(updatedPatient.getFees());
            patient.setUrgency(updatedPatient.getUrgency());

            return ResponseEntity.ok(patientRepository.save(patient));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================
    // 🔥 FIXED ADD MEDICINE (MAIN LOGIC)
    // =========================
    @PutMapping("/{id}/add-medicine")
    public ResponseEntity<?> assignMedicineToPatient(
            @PathVariable Long id,
            @RequestBody List<MedicineWithTime> medicinesWithTime) {

        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }

        Patient patient = patientOpt.get();
        List<Prescription> prescriptions = patient.getPrescription();

        if (prescriptions == null) {
            prescriptions = new ArrayList<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // 🔥 SAME TIME FOR ENTIRE VISIT
        LocalDateTime visitTime = LocalDateTime.now();

        for (MedicineWithTime item : medicinesWithTime) {

            Medicine medicine = medicineService.getMedicineByName(item.getMedicineName());

            if (medicine == null) {
                return ResponseEntity.badRequest()
                        .body("Medicine not found: " + item.getMedicineName());
            }

            try {
                Prescription p = new Prescription();

                p.setPatient(patient);
                p.setMedicine(medicine);
                p.setDosage("1 tablet");

                // 🔥 FIXES
                p.setCreatedAt(visitTime); // SAME TIME
                p.setSymptoms(item.getSymptoms());
                p.setDiagnosis(item.getDiagnosis());

                p.setTimeToTake(
                        objectMapper.writeValueAsString(
                                new HashSet<>(item.getTimeToTake())
                        )
                );

                prescriptions.add(p);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing prescription");
            }
        }

        patient.setPrescription(prescriptions);
        patientRepository.save(patient);

        return ResponseEntity.ok("Medicines assigned successfully");
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            if (!patientRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Patient not found"));
            }

            patientRepository.deleteById(id);

            return ResponseEntity.ok(Map.of("message", "Patient deleted successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting patient"));
        }
    }

    // =========================
    // SEARCH
    // =========================
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam("query") String query) {
        return ResponseEntity.ok(patientRepository.searchByNameOrId(query));
    }

    // =========================
    // MEDICINES LIST
    // =========================
    @GetMapping("/medicines")
    public List<Medicine> getAllMedicines() {
        return medicineService.getAllMedicines();
    }
}