package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.domain.repository.DoctorRepository;
import kigali.clinic.rw.domain.repository.OfficeRepository;
import kigali.clinic.rw.domain.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final OfficeRepository officeRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         SpecializationRepository specializationRepository,
                         OfficeRepository officeRepository) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
        this.officeRepository = officeRepository;
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

    public List<Doctor> findBySpecialization(String name) {
        return doctorRepository.findBySpecializationNameIgnoreCase(name);
    }

    public List<Doctor> findDoctorsWithoutOffice() {
        return doctorRepository.findDoctorsWithoutOffice();
    }

    // A1 - add specialization to doctor
    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specOpt = specializationRepository.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();

        // check if already has it
        if (doctor.getSpecializations().contains(spec)) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(spec);
        doctorRepository.save(doctor);
        return "Specialization added to the doctor";
    }

    // A2 - remove specialization
    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specOpt = specializationRepository.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();

        if (!doctor.getSpecializations().contains(spec)) {
            return "The doctor does not have that specialization";
        }

        doctor.getSpecializations().remove(spec);
        doctorRepository.save(doctor);
        return "Specialization removed from the doctor";
    }

    // A4 - move doctor to another office
    public String changeOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Office> officeOpt = officeRepository.findByOfficeNumber(officeNumber);
        if (officeOpt.isEmpty()) {
            return "The office with that office number does not exist";
        }

        Doctor doctor = doctorOpt.get();
        Office newOffice = officeOpt.get();

        // check if office is taken by another doctor
        if (doctorRepository.existsByOfficeId(newOffice.getId())) {
            if (doctor.getOffice() == null || !doctor.getOffice().getId().equals(newOffice.getId())) {
                return "That office is taken by other doctor";
            }
        }

        doctor.setOffice(newOffice);
        doctorRepository.save(doctor);
        return "Office changed successfully";
    }

    // D3
    public List<Doctor> searchByName(String name) {
        return doctorRepository.searchByName(name);
    }

    // bonus
    public List<Object[]> doctorWorkload(LocalDate start, LocalDate end) {
        return doctorRepository.doctorWorkload(start, end);
    }
}
