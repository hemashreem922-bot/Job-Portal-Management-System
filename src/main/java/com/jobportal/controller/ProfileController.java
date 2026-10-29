package com.jobportal.controller;

import com.jobportal.dto.CompanyDto;
import com.jobportal.dto.UserProfileDto;
import com.jobportal.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/candidate/{userId}")
    public ResponseEntity<UserProfileDto> getCandidateProfile(@PathVariable Long userId) {
        UserProfileDto profile = profileService.getCandidateProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/candidate/{userId}")
    public ResponseEntity<UserProfileDto> updateCandidateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDto dto) {
        UserProfileDto updated = profileService.updateCandidateProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/company/{userId}")
    public ResponseEntity<CompanyDto> getCompanyProfile(@PathVariable Long userId) {
        CompanyDto profile = profileService.getCompanyProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/company/{userId}")
    public ResponseEntity<CompanyDto> updateCompanyProfile(
            @PathVariable Long userId,
            @RequestBody CompanyDto dto) {
        CompanyDto updated = profileService.updateCompanyProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }
}
