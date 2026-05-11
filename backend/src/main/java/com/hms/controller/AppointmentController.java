package com.hms.controller;

import com.hms.model.Appointment;
import com.hms.service.AppointmentService;
import com.hms.service.DoctorService;
import com.hms.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        // Ensure patient and doctor exist
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null ||
            appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        java.util.Optional<com.hms.model.Patient> patientOpt = patientService.getPatientById(appointment.getPatient().getId());
        java.util.Optional<com.hms.model.Doctor> doctorOpt = doctorService.getDoctorById(appointment.getDoctor().getId());
        
        if (!patientOpt.isPresent() || !doctorOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        appointment.setPatient(patientOpt.get());
        appointment.setDoctor(doctorOpt.get());
        
        return ResponseEntity.ok(appointmentService.saveAppointment(appointment));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentService.getAppointmentById(id).map(existingAppointment -> {
            existingAppointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
            existingAppointment.setStatus(appointmentDetails.getStatus());
            existingAppointment.setNotes(appointmentDetails.getNotes());
            // Update relations if provided
            if (appointmentDetails.getDoctor() != null && appointmentDetails.getDoctor().getId() != null) {
                doctorService.getDoctorById(appointmentDetails.getDoctor().getId())
                             .ifPresent(existingAppointment::setDoctor);
            }
            Appointment updatedAppointment = appointmentService.saveAppointment(existingAppointment);
            return ResponseEntity.ok(updatedAppointment);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (appointmentService.getAppointmentById(id).isPresent()) {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
