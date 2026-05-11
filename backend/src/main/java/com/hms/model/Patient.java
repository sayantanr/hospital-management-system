package com.hms.model;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "patients")
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private LocalDate dateOfBirth;
    
    private String gender;
    
    private String contactNumber;
    
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;
}
