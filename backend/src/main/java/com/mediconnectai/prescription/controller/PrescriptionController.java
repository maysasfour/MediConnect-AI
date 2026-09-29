package com.mediconnectai.prescription.controller;
import com.mediconnectai.prescription.entity.Prescription;
import com.mediconnectai.prescription.service.PrescriptionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {
    private final PrescriptionService service;
    public PrescriptionController(PrescriptionService service) { this.service = service; }
    @GetMapping("/patient/{patientId}") public List<Prescription> findByPatient(@PathVariable String patientId) { return service.findByPatient(patientId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Prescription create(@RequestBody Prescription prescription) { return service.create(prescription); }
}
