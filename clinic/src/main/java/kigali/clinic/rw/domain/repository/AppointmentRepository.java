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

    // old derived
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    // A4 / B3 – doctor already booked (non-cancelled)
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, LocalDate date, AppointmentStatus status);

    // old C1
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    // old C4
    @Modifying
    @Query("UPDATE Appointment a SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED " +
           "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED")
    int cancelAppointmentsForDoctorOnDate(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);

    Page<Appointment> findAll(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED AND a.appointmentDate < :date")
    int deleteCancelledBefore(@Param("date") LocalDate date);

    // D1 – Doctor agenda for a day (Fixed with explicit JPQL query to prevent 500 crashes)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :status ORDER BY a.appointmentDate ASC")
    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
            @Param("doctorId") UUID doctorId, 
            @Param("date") LocalDate date, 
            @Param("status") AppointmentStatus status);

    // D2 – Upcoming appointments of a patient (DERIVED)
    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber, LocalDate today, List<AppointmentStatus> statuses);

    // for dashboard
    long countByAppointmentDate(LocalDate date);

    long countByStatus(AppointmentStatus status);

    // C4 – check if appointment has prescriptions
    boolean existsByIdAndPrescriptionsIsNotEmpty(UUID id);
}
