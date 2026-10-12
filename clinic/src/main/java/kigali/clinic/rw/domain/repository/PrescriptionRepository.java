package kigali.clinic.rw.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    // C2 – DERIVED
    List<Prescription> findByAppointmentId(UUID appointmentId);

    // C3 – JPQL
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.ssNumber = :ssNumber ORDER BY p.issuedDate DESC")
    List<Prescription> findByPatientSsNumber(@Param("ssNumber") String ssNumber);

    // C4 – check if appointment has prescriptions
    boolean existsByAppointmentId(UUID appointmentId);

    // Bonus – top medicines
    @Query("SELECT p.medicineName, COUNT(p) FROM Prescription p GROUP BY p.medicineName ORDER BY COUNT(p) DESC")
    List<Object[]> topMedicines();
}
