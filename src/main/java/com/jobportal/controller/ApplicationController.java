package com.jobportal.controller;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.StatusUpdateRequest;
import com.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(
            @RequestParam Long candidateId,
            @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.apply(request, candidateId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@RequestParam Long candidateId) {
        List<ApplicationResponse> applications = applicationService.getCandidateApplications(candidateId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<ApplicationResponse>> getEmployerApplications(@PathVariable Long employerId) {
        List<ApplicationResponse> applications = applicationService.getEmployerApplications(employerId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam Long employerId) {
        List<ApplicationResponse> applications = applicationService.getJobApplications(jobId, employerId);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody StatusUpdateRequest request) {
        ApplicationResponse updated = applicationService.updateStatus(id, request, userId);
        return ResponseEntity.ok(updated);
    }
}
