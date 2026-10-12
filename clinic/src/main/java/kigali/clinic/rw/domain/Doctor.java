package kigali.clinic.rw.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "doctor")
@JsonIgnoreProperties({"appointments"})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Owning side of Many-to-Many (as required by Quiz 2)
    @ManyToMany
    @JoinTable(
        name = "doctor_specialization",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private List<Specialization> specializations = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    // Owning side of One-to-One (as required by Quiz 2 hint)
    @OneToOne
    @JoinColumn(name = "office_id")
    private Office office;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public List<Specialization> getSpecializations() { return specializations; }
    public void setSpecializations(List<Specialization> specializations) { this.specializations = specializations; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public Office getOffice() { return office; }
    public void setOffice(Office office) { this.office = office; }
}
