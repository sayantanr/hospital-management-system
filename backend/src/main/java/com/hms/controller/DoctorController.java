package com.hms.controller;

import com.hms.model.Doctor;
import com.hms.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.saveDoctor(doctor);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorService.getDoctorById(id).map(existingDoctor -> {
            existingDoctor.setFirstName(doctorDetails.getFirstName());
            existingDoctor.setLastName(doctorDetails.getLastName());
            existingDoctor.setSpecialization(doctorDetails.getSpecialization());
            existingDoctor.setContactNumber(doctorDetails.getContactNumber());
            existingDoctor.setEmail(doctorDetails.getEmail());
            existingDoctor.setDutySchedule(doctorDetails.getDutySchedule());
            Doctor updatedDoctor = doctorService.saveDoctor(existingDoctor);
            return ResponseEntity.ok(updatedDoctor);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        if (doctorService.getDoctorById(id).isPresent()) {
            doctorService.deleteDoctor(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
