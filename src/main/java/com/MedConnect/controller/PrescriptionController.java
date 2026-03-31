package com.MedConnect.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.MedConnect.entity.Patient;
import com.MedConnect.entity.Prescription;
import com.MedConnect.repository.PrescriptionRepository;
import com.MedConnect.service.PatientService;
import com.MedConnect.service.TwilioService;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@CrossOrigin(origins = "https://medconnect-frontend-1.onrender.com")
@org.springframework.context.annotation.Profile("!demo")
@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private PatientService patientService; // Autowire PatientService

    // Add prescription for a patient
    @PostMapping
    public Prescription addPrescription(@RequestBody Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    // Get prescriptions for a specific patient
    @GetMapping("/{patientId}")
    public List<Prescription> getPrescriptionsForPatient(@PathVariable Long patientId) {
    	return prescriptionRepository.findByPatientId(patientId);
    }

    // Send prescription to a patient's WhatsApp
    @PutMapping("/patients/{id}/send-prescription")
    public ResponseEntity<String> sendPrescription(@PathVariable Long id) {
        try {
            Patient patient = patientService.getPatientById(id);

            if (patient == null || patient.getPhoneNumber() == null) {
                return ResponseEntity.badRequest().body("Patient or phone number not found.");
            }

            // 🔽 Define the external directory where PDF will be stored
            String basePath = "/tmp/uploads/prescriptions/";  // Use /tmp directory on Render
            String pdfFileName = id + ".pdf";
            String pdfPath = basePath + pdfFileName;

            // 🔽 Generate the PDF and save it
            createPdfForPatient(patient, pdfPath);

            // 🔽 Public URL pointing to your new custom endpoint
            String pdfUrl = "https://medconnect-backend-sms3.onrender.com/api/v1/prescriptions/files/" + pdfFileName;

            // 🔽 WhatsApp message caption
            String caption = "Hello " + patient.getName() + ", please find your prescription attached.";

            // 🔽 Send the PDF via WhatsApp
            twilioService.sendWhatsAppMessageWithMedia(patient.getPhoneNumber(), pdfUrl, caption);

            return ResponseEntity.ok("Prescription PDF sent successfully via WhatsApp!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending prescription!");
        }
    }

    
 // ⬇️ Put the PDF creation method right here
    private void createPdfForPatient(Patient patient, String filePath) throws IOException {
        File directory = new File(filePath).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Logo
        Image logo = new Image(ImageDataFactory.create("https://medconnect-frontend-1.onrender.com/assets/medconnect2-logo.png"));
        logo.setWidth(90).setHeight(90);
        logo.setHorizontalAlignment(HorizontalAlignment.CENTER); // Center align the logo
        document.add(logo);

        // Date & Time
        String dateTime = java.time.LocalDateTime.now().toString().replace("T", ", ");
        document.add(new Paragraph("Date & Time: " + dateTime).setBold());

        // Title
        document.add(new Paragraph("Patient Prescription Details").setBold().setFontSize(14).setMarginTop(10));

        // Table for patient data
        float[] columnWidths = {1, 2}; // ID & Data column; feel free to adjust ratio
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100)); // Full page width

        // Helper method to add rows
        table.addCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(patient.getId()))));

        table.addCell(new Cell().add(new Paragraph("Name").setBold()));
        table.addCell(new Cell().add(new Paragraph(patient.getName())));

        table.addCell(new Cell().add(new Paragraph("Age").setBold()));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(patient.getAge()))));

        table.addCell(new Cell().add(new Paragraph("Blood Group").setBold()));
        table.addCell(new Cell().add(new Paragraph(patient.getBlood())));

        table.addCell(new Cell().add(new Paragraph("Dose").setBold()));
        table.addCell(new Cell().add(new Paragraph(patient.getDose()))); // Make sure 'dose' is in Patient entity

        
        table.addCell(new Cell().add(new Paragraph("Fees").setBold()));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(patient.getFees()))));

        document.add(table.setMarginTop(10).setMarginBottom(20));

       
        // Retrieve the prescriptions for the patient
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patient.getId());

        if (!prescriptions.isEmpty()) {
            document.add(new Paragraph("Medicines:").setBold().setFontSize(12).setMarginTop(10));

            // Create a Table with columns for prescription details
            Table medTable = new Table(new float[]{4,2,4});  // Adjust column widths if needed
            medTable.setWidth(UnitValue.createPercentValue(100));

            // Add table headers for the columns
            medTable.addCell(new Cell().add(new Paragraph("Drug Name").setBold()));
            medTable.addCell(new Cell().add(new Paragraph("Time To Take").setBold()));
            medTable.addCell(new Cell().add(new Paragraph("Dosage").setBold()));
            
          
            // Loop through each prescription and add the details to the table
            for (Prescription p : prescriptions) {
                String drugName = (p.getMedicine() != null) ? p.getMedicine().getDrugName() : "N/A";
                String timeToTake = (p.getTimeToTake() != null) ? String.join(", ", p.getTimeToTake()) : "N/A";
                String dosage = (p.getDosage() != null) ? p.getDosage() : "N/A";

                medTable.addCell(new Cell().add(new Paragraph(drugName)));
                medTable.addCell(new Cell().add(new Paragraph(timeToTake)));
                medTable.addCell(new Cell().add(new Paragraph(dosage)));
            }

            // Add the table to the document
            document.add(medTable);
        }
        
     // Doctor details
        document.add(new Paragraph("Dr. Kadam").setBold().setMarginTop(20));
        document.add(new Paragraph("(BHMS) Bachelor of Homeopathic Medicine and Surgery"));
        document.add(new Paragraph("Phone: +91 9699-590-048"));
       

        document.close();
        System.out.println("PDF created at: " + filePath);
    }




    	@GetMapping("/files/{filename:.+}")
    	public ResponseEntity<?> servePrescriptionPdf(@PathVariable String filename) {
        try {
            String filePath =  "/tmp/uploads/prescriptions/" + filename;
            File file = new File(filePath);
            System.out.println("Trying to serve file from: " + filePath);

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file.");
        }
        
    }



   

}
