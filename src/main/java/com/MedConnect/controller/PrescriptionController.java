package com.MedConnect.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.MedConnect.entity.Patient;
import com.MedConnect.entity.Prescription;
import com.MedConnect.repository.PrescriptionRepository;
import com.MedConnect.service.PatientService;
import com.MedConnect.service.TwilioService;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

@CrossOrigin(origins = "https://medconnect-frontend-1.onrender.com")
@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private PatientService patientService;

    // ✅ Add prescription
   @PostMapping
public Prescription addPrescription(@RequestBody Prescription prescription) {

    // ✅ ADD THIS LINE
    prescription.setCreatedAt(java.time.LocalDateTime.now());

    return prescriptionRepository.save(prescription);
}

    // ✅ GET prescriptions (FIXED - no 500 error)
  @GetMapping("/{patientId}")
public ResponseEntity<?> getPrescriptionsForPatient(@PathVariable Long patientId) {
    try {
        List<Prescription> list = prescriptionRepository.findByPatientId(patientId);

        // 🔥 SAFE HANDLING (IMPORTANT)
        list.forEach(p -> {
            try {
                // force fetch medicine safely
                if (p.getMedicine() != null) {
                    p.getMedicine().getDrugName();
                }
            } catch (Exception e) {
                p.setMedicine(null); // avoid crash
            }
        });

        return ResponseEntity.ok(list != null ? list : List.of());

    } catch (Exception e) {
        e.printStackTrace(); // 🔥 MUST KEEP
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage()); // 🔥 SHOW REAL ERROR
    }
}
    // ✅ Send prescription PDF
    @PutMapping("/patients/{id}/send-prescription")
    public ResponseEntity<String> sendPrescription(@PathVariable Long id) {
        try {
            Patient patient = patientService.getPatientById(id);

            if (patient == null || patient.getPhoneNumber() == null) {
                return ResponseEntity.badRequest().body("Patient or phone number not found.");
            }

            String basePath = "/tmp/uploads/prescriptions/";
            String pdfFileName = id + ".pdf";
            String pdfPath = basePath + pdfFileName;

            createPdfForPatient(patient, pdfPath);

            String pdfUrl = "https://medconnect-backend-sms3.onrender.com/api/v1/prescriptions/files/" + pdfFileName;

            String caption = "Hello " + patient.getName() + ", please find your prescription attached.";

            twilioService.sendWhatsAppMessageWithMedia(patient.getPhoneNumber(), pdfUrl, caption);

            return ResponseEntity.ok("Prescription sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending prescription!");
        }
    }

    // ✅ PDF creation (FIXED TIME BUG)
    private void createPdfForPatient(Patient patient, String filePath) throws IOException {

        File directory = new File(filePath).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Logo
        Image logo = new Image(ImageDataFactory.create(
                "https://medconnect-frontend-1.onrender.com/assets/medconnect2-logo.png"));
        logo.setWidth(90);
        logo.setHeight(90);
        logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(logo);

        // Date
        String dateTime = java.time.LocalDateTime.now().toString().replace("T", ", ");
        document.add(new Paragraph("Date & Time: " + dateTime).setBold());

        // Title
        document.add(new Paragraph("Patient Prescription Details").setBold().setFontSize(14));

        // Patient table
        Table table = new Table(new float[]{1, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell("ID"); table.addCell(String.valueOf(patient.getId()));
        table.addCell("Name"); table.addCell(patient.getName());
        table.addCell("Age"); table.addCell(String.valueOf(patient.getAge()));
        table.addCell("Blood Group"); table.addCell(patient.getBlood());
        table.addCell("Dose"); table.addCell(patient.getDose());
        table.addCell("Fees"); table.addCell(String.valueOf(patient.getFees()));

        document.add(table);

        // Medicines
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patient.getId());

        if (!prescriptions.isEmpty()) {

            document.add(new Paragraph("Medicines:").setBold());

            Table medTable = new Table(new float[]{4, 2, 4});
            medTable.setWidth(UnitValue.createPercentValue(100));

            medTable.addCell("Drug Name");
            medTable.addCell("Time To Take");
            medTable.addCell("Dosage");

            for (Prescription p : prescriptions) {

                String drugName = (p.getMedicine() != null)
                        ? p.getMedicine().getDrugName()
                        : "N/A";

                // ✅ FIXED HERE
                String timeToTake = (p.getTimeToTake() != null && !p.getTimeToTake().isEmpty())
                        ? String.join(", ", p.getTimeToTake())
                        : "N/A";

                String dosage = (p.getDosage() != null) ? p.getDosage() : "N/A";

                medTable.addCell(drugName);
                medTable.addCell(timeToTake);
                medTable.addCell(dosage);
            }

            document.add(medTable);
        }

        document.add(new Paragraph("Dr. Kadam").setBold());
        document.add(new Paragraph("(BHMS)"));
        document.add(new Paragraph("Phone: +91 9699-590-048"));

        document.close();
    }

    // ✅ Serve PDF
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<?> servePrescriptionPdf(@PathVariable String filename) {
        try {
            String filePath = "/tmp/uploads/prescriptions/" + filename;
            File file = new File(filePath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                    .header("Content-Type", "application/pdf")
                    .body(fileContent);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file.");
        }
    }
    @GetMapping("/history/{patientId}")
public List<Prescription> getPatientHistory(@PathVariable Long patientId) {
    return prescriptionRepository.findByPatientId(patientId);
}
@GetMapping("/last/{patientId}")
public ResponseEntity<?> getLastPrescription(@PathVariable Long patientId) {

    List<Prescription> list =
        prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

    if (list == null || list.isEmpty()) {
        return ResponseEntity.ok(null);
    }

    // return latest (first)
    return ResponseEntity.ok(list.get(0));
}
}