package com.hms.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private String specialization;
    
    private String contactNumber;
    
    private String email;
    
    private String dutySchedule; // e.g., "Mon-Fri 9AM-5PM"
}
