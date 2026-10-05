package kigali.clinic.rw.domain;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "office")
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "office_number")
    private int officeNumber;

    @OneToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(int officeNumber) { this.officeNumber = officeNumber; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
