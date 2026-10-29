package com.jobportal.controller;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long userId) {
        List<JobResponse> jobs = jobService.searchJobs(keyword, category, jobType, location, userId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        JobResponse job = jobService.getJobById(id, userId);
        return ResponseEntity.ok(job);
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestParam Long employerId,
            @Valid @RequestBody JobRequest request) {
        JobResponse created = jobService.createJob(request, employerId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @RequestParam Long employerId,
            @Valid @RequestBody JobRequest request) {
        JobResponse updated = jobService.updateJob(id, request, employerId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id,
            @RequestParam Long userId) {
        jobService.deleteJob(id, userId);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<JobResponse>> getEmployerJobs(@PathVariable Long employerId) {
        List<JobResponse> jobs = jobService.getEmployerJobs(employerId);
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> toggleSaveJob(
            @PathVariable Long id,
            @RequestParam Long userId) {
        boolean saved = jobService.toggleSaveJob(id, userId);
        return ResponseEntity.ok(Map.of(
                "saved", saved,
                "message", saved ? "Job saved to bookmarks" : "Job removed from bookmarks"
        ));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<JobResponse>> getSavedJobs(@RequestParam Long userId) {
        List<JobResponse> savedJobs = jobService.getSavedJobs(userId);
        return ResponseEntity.ok(savedJobs);
    }
}
