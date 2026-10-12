package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // C1
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID appointmentId,
                                       @RequestBody Prescription prescription) {
        String message = prescriptionService.savePrescription(appointmentId, prescription);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("only for COMPLETED")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // C2
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> byAppointment(@PathVariable UUID appointmentId) {
        Object result = prescriptionService.findByAppointment(appointmentId);
        if (result instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    // C3
    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> byPatient(@PathVariable String ssNumber) {
        return prescriptionService.findByPatientSsNumber(ssNumber);
    }

    // C4 update
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Prescription body) {
        String message = prescriptionService.updatePrescription(id, body);
        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // C4 delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = prescriptionService.deletePrescription(id);
        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // bonus
    @GetMapping("/stats/top-medicines")
    public List<Object[]> topMedicines() {
        return prescriptionService.topMedicines();
    }
}
