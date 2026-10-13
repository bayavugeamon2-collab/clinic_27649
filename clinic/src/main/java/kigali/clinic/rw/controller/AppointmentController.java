package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

    // B1 - Safe booking
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Appointment appointment) {
        Object result = appointmentService.saveAppointment(appointment);

        if (result instanceof String) {
            String msg = (String) result;
            if (msg.contains("past")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
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

    // C4 - Protected delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = appointmentService.deleteAppointment(id);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("prescriptions")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok(message);
    }

    @GetMapping("/by-status")
    public List<Appointment> byStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.findByStatus(status);
    }

    @GetMapping("/between")
    public List<Appointment> between(@RequestParam String start, @RequestParam String end) {
        return appointmentService.findBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/stats/by-status")
    public List<Object[]> statsByStatus() {
        return appointmentService.countByStatus();
    }

    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, LocalDate.parse(date));
    }

    @GetMapping("/page")
    public Page<Appointment> page(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return appointmentService.getPage(pageable);
    }

    @DeleteMapping("/cancelled-before")
    public String cancelledBefore(@RequestParam String date) {
        return appointmentService.deleteCancelledBefore(LocalDate.parse(date));
    }

    // B2 - Status flow
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id,
                                               @RequestParam AppointmentStatus status) {
        String message = appointmentService.updateStatus(id, status);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("Cannot change")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(message);
    }

    // B3 - Reschedule
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable UUID id,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate) {
        String message = appointmentService.reschedule(id, newDate);

        if (message.contains("does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message.contains("cannot be rescheduled") || message.contains("past")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if (message.contains("already booked")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok(message);
    }
}
