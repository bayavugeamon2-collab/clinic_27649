package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PostMapping
    public Specialization save(@RequestBody Specialization specialization) {
        return specializationService.saveSpecialization(specialization);
    }

    @GetMapping
    public List<Specialization> getAll() {
        return specializationService.getAllSpecializations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialization> getById(@PathVariable UUID id) {
        return specializationService.getSpecializationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        specializationService.deleteSpecialization(id);
    }

    // B3
    @GetMapping("/unused")
    public List<Specialization> unused() {
        return specializationService.findUnused();
    }
}
