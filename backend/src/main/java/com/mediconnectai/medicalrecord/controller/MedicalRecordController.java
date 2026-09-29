package com.mediconnectai.medicalrecord.controller;

import com.mediconnectai.medicalrecord.entity.Encounter;
import com.mediconnectai.medicalrecord.service.MedicalRecordService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medical-records")
public class MedicalRecordController {
    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @GetMapping("/patient/{patientId}")
    public List<Encounter> findByPatient(@PathVariable String patientId) {
        return service.findByPatient(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Encounter create(@RequestBody Encounter encounter) {
        return service.create(encounter);
    }
}
