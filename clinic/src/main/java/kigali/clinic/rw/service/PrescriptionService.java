package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.domain.repository.AppointmentRepository;
import kigali.clinic.rw.domain.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // C1 - write prescription
    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = opt.get();

        // only for COMPLETED appointments
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepository.save(prescription);
        return "Prescription saved successfully";
    }

    // C2
    public Object findByAppointment(UUID appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            return "The appointment with that id does not exist";
        }
        return prescriptionRepository.findByAppointmentId(appointmentId);
    }

    // C3
    public List<Prescription> findByPatientSsNumber(String ssNumber) {
        return prescriptionRepository.findByPatientSsNumber(ssNumber);
    }

    // C4 update
    public String updatePrescription(UUID id, Prescription body) {
        Optional<Prescription> opt = prescriptionRepository.findById(id);
        if (opt.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        Prescription existing = opt.get();
        existing.setMedicineName(body.getMedicineName());
        existing.setDosage(body.getDosage());
        existing.setDurationDays(body.getDurationDays());
        prescriptionRepository.save(existing);
        return "Prescription updated successfully";
    }

    // C4 delete
    public String deletePrescription(UUID id) {
        if (!prescriptionRepository.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepository.deleteById(id);
        return "Prescription deleted successfully";
    }

    // bonus
    public List<Object[]> topMedicines() {
        return prescriptionRepository.topMedicines();
    }
}
