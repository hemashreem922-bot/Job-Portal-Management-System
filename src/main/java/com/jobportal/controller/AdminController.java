package com.jobportal.controller;

import com.jobportal.dto.AdminStatsDto;
import com.jobportal.model.User;
import com.jobportal.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        AdminStatsDto stats = adminService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        boolean active = adminService.toggleUserStatus(userId);
        return ResponseEntity.ok(Map.of(
                "active", active,
                "message", active ? "User account enabled" : "User account disabled"
        ));
    }

    @PutMapping("/jobs/{jobId}/status")
    public ResponseEntity<?> updateJobStatus(
            @PathVariable Long jobId,
            @RequestParam String status) {
        adminService.updateJobStatus(jobId, status);
        return ResponseEntity.ok(Map.of("message", "Job status updated to " + status));
    }
}
