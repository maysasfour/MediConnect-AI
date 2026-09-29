package com.mediconnectai.aiassistant.controller;
import com.mediconnectai.aiassistant.entity.AISymptomSummary;
import com.mediconnectai.aiassistant.service.AIService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/ai")
public class AIController {
    private final AIService service;
    public AIController(AIService service) { this.service = service; }
    @PostMapping("/symptom-summary")
    public AISymptomSummary summarize(@Valid @RequestBody SummaryRequest request) { return service.summarize(request.patientId(), request.symptoms()); }
    public record SummaryRequest(String patientId, @NotBlank String symptoms) {}
}
