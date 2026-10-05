package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // A4 – path /api/appointments/save
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Appointment appointment) {
        Object result = appointmentService.saveAppointment(appointment);
        if (result instanceof String) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<Appointment> getAll() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
    }

    // A2
    @GetMapping("/by-status")
    public List<Appointment> byStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.findByStatus(status);
    }

    // A3
    @GetMapping("/between")
    public List<Appointment> between(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return appointmentService.findBetween(startDate, endDate);
    }

    // C1
    @GetMapping("/stats/by-status")
    public List<Object[]> statsByStatus() {
        return appointmentService.countByStatus();
    }

    // C4
    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return appointmentService.cancelDay(doctorId, localDate);
    }

    // Bonus page
    @GetMapping("/page")
    public Page<Appointment> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {

        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return appointmentService.getPage(pageable);
    }

    // Bonus delete
    @DeleteMapping("/cancelled-before")
    public String cancelledBefore(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return appointmentService.deleteCancelledBefore(localDate);
    }
}
