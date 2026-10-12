package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public Doctor save(@RequestBody Doctor doctor) {
        return doctorService.saveDoctor(doctor);
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
    }

    @GetMapping("/by-specialization")
    public List<Doctor> bySpecialization(@RequestParam String name) {
        return doctorService.findBySpecialization(name);
    }

    @GetMapping("/without-office")
    public List<Doctor> withoutOffice() {
        return doctorService.findDoctorsWithoutOffice();
    }

    // A1
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(@PathVariable UUID doctorId,
                                                    @PathVariable UUID specializationId) {
        String message = doctorService.addSpecialization(doctorId, specializationId);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("already has")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // A2
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(@PathVariable UUID doctorId,
                                                       @PathVariable UUID specializationId) {
        String message = doctorService.removeSpecialization(doctorId, specializationId);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("does not have")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // A4
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> changeOffice(@PathVariable UUID doctorId,
                                               @RequestParam int officeNumber) {
        String message = doctorService.changeOffice(doctorId, officeNumber);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("taken")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // D1
    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> agenda(@PathVariable UUID doctorId, @RequestParam String date) {
        Object result = appointmentService.getAgenda(doctorId, LocalDate.parse(date));

        if (result instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    // D3
    @GetMapping("/search")
    public List<Doctor> search(@RequestParam String name) {
        return doctorService.searchByName(name);
    }

    // bonus
    @GetMapping("/workload")
    public List<Object[]> workload(@RequestParam String start, @RequestParam String end) {
        return doctorService.doctorWorkload(LocalDate.parse(start), LocalDate.parse(end));
    }
}
