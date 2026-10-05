package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PostMapping
    public Office save(@RequestBody Office office) {
        return officeService.saveOffice(office);
    }

    @GetMapping
    public List<Office> getAll() {
        return officeService.getAllOffices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Office> getById(@PathVariable UUID id) {
        return officeService.getOfficeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        officeService.deleteOffice(id);
    }

    // C3
    @GetMapping("/busiest")
    public Object busiest() {
        return officeService.getBusiestOffice();
    }
}
