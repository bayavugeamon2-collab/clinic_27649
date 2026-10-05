package kigali.clinic.rw.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // A2 – DERIVED
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    // A3 – DERIVED
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    // A4 – DERIVED
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    // C1 – JPQL
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    // C4 – JPQL UPDATE
    @Modifying
    @Query("UPDATE Appointment a SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED " +
           "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED")
    int cancelAppointmentsForDoctorOnDate(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);

    // Bonus – Pageable
    Page<Appointment> findAll(Pageable pageable);

    // Bonus – JPQL DELETE
    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED AND a.appointmentDate < :date")
    int deleteCancelledBefore(@Param("date") LocalDate date);
}
