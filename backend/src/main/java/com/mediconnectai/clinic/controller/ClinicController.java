package com.mediconnectai.clinic.controller;

import com.mediconnectai.clinic.entity.Clinic;
import com.mediconnectai.clinic.service.ClinicService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clinics")
public class ClinicController {
    private final ClinicService service;

    public ClinicController(ClinicService service) {
        this.service = service;
    }

    @GetMapping
    public List<Clinic> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Clinic findById(@PathVariable String id) {
        return service.findById(id);
    }
}
