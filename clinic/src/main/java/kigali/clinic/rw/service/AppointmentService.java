package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // A4 – modified save
    public Object saveAppointment(Appointment appointment) {
        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);

        if (booked) {
            return "Doctor is already booked on that date";
        }

        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }

    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

    // A2
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    // C1
    public List<Object[]> countByStatus() {
        return appointmentRepository.countByStatus();
    }

    // C4
    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int count = appointmentRepository.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return count + " appointments cancelled";
    }

    // Bonus page
    public Page<Appointment> getPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    // Bonus delete
    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int count = appointmentRepository.deleteCancelledBefore(date);
        return count + " appointments deleted";
    }
}
