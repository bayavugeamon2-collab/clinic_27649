package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.domain.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public Specialization saveSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specializationRepository.findById(id);
    }

    public void deleteSpecialization(UUID id) {
        specializationRepository.deleteById(id);
    }

    // B3
    public List<Specialization> findUnused() {
        return specializationRepository.findUnusedSpecializations();
    }
}
