package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepository.findById(id);
    }

    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }

    // B1
    public List<Doctor> findBySpecialization(String name) {
        return doctorRepository.findBySpecializationNameIgnoreCase(name);
    }

    // B2
    public List<Doctor> findDoctorsWithoutOffice() {
        return doctorRepository.findDoctorsWithoutOffice();
    }
}
