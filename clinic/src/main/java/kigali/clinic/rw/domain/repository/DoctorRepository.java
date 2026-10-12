package kigali.clinic.rw.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    // old B1
    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationNameIgnoreCase(@Param("name") String name);

    // old B2
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findDoctorsWithoutOffice();

    boolean existsByOfficeId(UUID officeId);

    // D1 – Doctor's agenda for a day (DERIVED)
    List<Appointment> findByIdAndAppointmentsAppointmentDateAndAppointmentsStatusNot(
            UUID doctorId, LocalDate date, AppointmentStatus status);

    // Better D1 – we will put the agenda query on AppointmentRepository

    // D3 – Search doctors by name (JPQL)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName ASC")
    List<Doctor> searchByName(@Param("name") String name);

    // Bonus – Doctor workload
    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d " +
           "WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.COMPLETED " +
           "AND a.appointmentDate BETWEEN :start AND :end " +
           "GROUP BY d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> doctorWorkload(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
