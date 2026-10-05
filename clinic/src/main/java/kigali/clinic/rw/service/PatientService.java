package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.repository.PatientRepository;
import kigali.clinic.rw.domain.repository.DoctorRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepository.findById(id);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

    // A1
    public List<Patient> findByLastName(String lastName) {
        return patientRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // B4
    public Object findPatientsOfDoctor(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            return "The doctor with that id does not exist";
        }
        return patientRepository.findPatientsOfDoctor(doctorId);
    }

    // C2
    public List<Patient> findFrequentPatients(long min) {
        return patientRepository.findFrequentPatients(min);
    }
}
