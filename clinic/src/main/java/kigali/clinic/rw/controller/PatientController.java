package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public Patient save(@RequestBody Patient patient) {
        return patientService.savePatient(patient);
    }

    @GetMapping
    public List<Patient> getAll() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/by-last-name")
    public List<Patient> byLastName(@RequestParam String lastName) {
        return patientService.findByLastName(lastName);
    }

    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> ofDoctor(@PathVariable UUID doctorId) {
        Object result = patientService.findPatientsOfDoctor(doctorId);
        if (result instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/frequent")
    public List<Patient> frequent(@RequestParam long min) {
        return patientService.findFrequentPatients(min);
    }

    // D2
    @GetMapping("/{ssNumber}/upcoming")
    public ResponseEntity<?> upcoming(@PathVariable String ssNumber) {
        Object result = appointmentService.getUpcoming(ssNumber);
        if (result instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
