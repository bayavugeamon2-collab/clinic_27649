package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.repository.AppointmentRepository;
import kigali.clinic.rw.domain.repository.DoctorRepository;
import kigali.clinic.rw.domain.repository.OfficeRepository;
import kigali.clinic.rw.domain.repository.PatientRepository;
import kigali.clinic.rw.domain.repository.PrescriptionRepository;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DashboardService(PatientRepository patientRepository,
                            DoctorRepository doctorRepository,
                            OfficeRepository officeRepository,
                            AppointmentRepository appointmentRepository,
                            PrescriptionRepository prescriptionRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // D4
    public Map<String, Object> getSummary() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("patients", patientRepository.count());
        map.put("doctors", doctorRepository.count());
        map.put("freeOffices", officeRepository.findFreeOffices().size());
        map.put("appointmentsToday", appointmentRepository.countByAppointmentDate(LocalDate.now()));
        map.put("scheduled", appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED));
        map.put("prescriptions", prescriptionRepository.count());
        return map;
    }
}
