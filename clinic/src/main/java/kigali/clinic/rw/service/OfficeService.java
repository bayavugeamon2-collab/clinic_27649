package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.repository.OfficeRepository;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public Office saveOffice(Office office) {
        return officeRepository.save(office);
    }

    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    public Optional<Office> getOfficeById(UUID id) {
        return officeRepository.findById(id);
    }

    public void deleteOffice(UUID id) {
        officeRepository.deleteById(id);
    }

    // A3
    public List<Office> findFreeOffices() {
        return officeRepository.findFreeOffices();
    }

    public Object getBusiestOffice() {
        List<Object[]> result = officeRepository.findBusiestOffice();
        if (result.isEmpty()) {
            return "No appointments yet";
        }
        return result.get(0);
    }
}
