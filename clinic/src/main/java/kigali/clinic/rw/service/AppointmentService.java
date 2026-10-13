package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.Arrays;
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
import kigali.clinic.rw.domain.repository.DoctorRepository;
import kigali.clinic.rw.domain.repository.PatientRepository;
import kigali.clinic.rw.domain.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PrescriptionRepository prescriptionRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // B1 - safe booking
    public Object saveAppointment(Appointment appointment) {
        // date cannot be in the past
        if (appointment.getAppointmentDate() != null &&
            appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        // always start as SCHEDULED
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // check if doctor is already booked
        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);

        if (booked) {
            return "Doctor is already booked on that date";
        }

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }

    // C4 - cannot delete if it has prescriptions
    public String deleteAppointment(UUID id) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        if (prescriptionRepository.existsByAppointmentId(id)) {
            return "That appointment has prescriptions, delete them first";
        }

        appointmentRepository.deleteById(id);
        return "Appointment deleted successfully";
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> countByStatus() {
        return appointmentRepository.countByStatus();
    }

    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int count = appointmentRepository.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return count + " appointments cancelled";
    }

    public Page<Appointment> getPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int count = appointmentRepository.deleteCancelledBefore(date);
        return count + " appointments deleted";
    }

    // B2 - status flow
    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = opt.get();
        AppointmentStatus current = appointment.getStatus();

        boolean allowed = false;

        if (current == AppointmentStatus.SCHEDULED) {
            if (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED) {
                allowed = true;
            }
        } else if (current == AppointmentStatus.CONFIRMED) {
            if (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED) {
                allowed = true;
            }
        }
        // COMPLETED and CANCELLED cannot change

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        return "Appointment status updated successfully";
    }

    // B3 - reschedule
    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = opt.get();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }

        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                newDate,
                AppointmentStatus.CANCELLED);

        if (booked) {
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
        return "Appointment rescheduled successfully";
    }

    // D1
    public Object getAgenda(UUID doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return appointmentRepository.findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    // D2
    public Object getUpcoming(String ssNumber) {
        if (patientRepository.findBySsNumber(ssNumber).isEmpty()) {
            return "That patient with that ssnumber does not exist";
        }

        List<AppointmentStatus> statuses = Arrays.asList(
                AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

        return appointmentRepository
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber, LocalDate.now(), statuses);
    }
}
