package com.hms.controller;

import com.hms.model.Patient;
import com.hms.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.savePatient(patient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientService.getPatientById(id).map(existingPatient -> {
            existingPatient.setFirstName(patientDetails.getFirstName());
            existingPatient.setLastName(patientDetails.getLastName());
            existingPatient.setDateOfBirth(patientDetails.getDateOfBirth());
            existingPatient.setGender(patientDetails.getGender());
            existingPatient.setContactNumber(patientDetails.getContactNumber());
            existingPatient.setEmail(patientDetails.getEmail());
            existingPatient.setAddress(patientDetails.getAddress());
            existingPatient.setMedicalHistory(patientDetails.getMedicalHistory());
            Patient updatedPatient = patientService.savePatient(existingPatient);
            return ResponseEntity.ok(updatedPatient);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.getPatientById(id).isPresent()) {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
