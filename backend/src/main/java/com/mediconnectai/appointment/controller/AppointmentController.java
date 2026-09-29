package com.mediconnectai.appointment.controller;

import com.mediconnectai.appointment.dto.AppointmentRequest;
import com.mediconnectai.appointment.dto.AppointmentResponse;
import com.mediconnectai.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppointmentResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody AppointmentRequest request) {
        return service.book(request);
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse changeStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return service.changeStatus(id, body.getOrDefault("status", "Pending"));
    }
}
