package com.jobportal.service;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.StatusUpdateRequest;
import com.jobportal.model.*;
import com.jobportal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CompanyRepository companyRepository;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository,
                              UserProfileRepository userProfileRepository,
                              CompanyRepository companyRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, Long candidateId) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        if (candidate.getRole() != Role.JOB_SEEKER) {
            throw new IllegalStateException("Only job seekers can apply for jobs");
        }

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (!"ACTIVE".equalsIgnoreCase(job.getStatus())) {
            throw new IllegalStateException("This job posting is closed and no longer accepting applications");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(job.getId(), candidateId)) {
            throw new IllegalStateException("You have already applied for this position");
        }

        String resumeUrl = request.getResumeUrl();
        if (resumeUrl == null || resumeUrl.isBlank()) {
            UserProfile profile = userProfileRepository.findByUserId(candidateId).orElse(null);
            if (profile != null && profile.getResumeUrl() != null && !profile.getResumeUrl().isBlank()) {
                resumeUrl = profile.getResumeUrl();
            }
        }

        JobApplication application = new JobApplication(job, candidate, request.getCoverLetter(), resumeUrl);
        application = applicationRepository.save(application);
        return toApplicationResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getCandidateApplications(Long candidateId) {
        return applicationRepository.findByCandidateIdOrderByAppliedAtDesc(candidateId).stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getEmployerApplications(Long employerId) {
        return applicationRepository.findByEmployerIdOrderByAppliedAtDesc(employerId).stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getJobApplications(Long jobId, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        User user = userRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.ADMIN && !job.getEmployer().getId().equals(employerId)) {
            throw new IllegalStateException("Not authorized to view applicants for this job");
        }

        return applicationRepository.findByJobIdOrderByAppliedAtDesc(jobId).stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, StatusUpdateRequest request, Long userId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isEmployer = application.getJob().getEmployer().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isEmployer && !isAdmin) {
            throw new IllegalStateException("You are not authorized to update this application");
        }

        application.setStatus(request.getStatus().toUpperCase());
        if (request.getRecruiterNotes() != null) {
            application.setRecruiterNotes(request.getRecruiterNotes());
        }
        application.setUpdatedAt(LocalDateTime.now());

        application = applicationRepository.save(application);
        return toApplicationResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplicationsAdmin() {
        return applicationRepository.findAll().stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse toApplicationResponse(JobApplication app) {
        ApplicationResponse resp = new ApplicationResponse();
        resp.setId(app.getId());
        resp.setJobId(app.getJob().getId());
        resp.setJobTitle(app.getJob().getTitle());
        resp.setJobCategory(app.getJob().getCategory());
        resp.setJobType(app.getJob().getJobType());
        resp.setJobLocation(app.getJob().getLocation());

        Company company = companyRepository.findByUserId(app.getJob().getEmployer().getId()).orElse(null);
        if (company != null) {
            resp.setCompanyName(company.getCompanyName());
        } else {
            resp.setCompanyName(app.getJob().getEmployer().getFullName());
        }

        resp.setCandidateId(app.getCandidate().getId());
        resp.setCandidateName(app.getCandidate().getFullName());
        resp.setCandidateEmail(app.getCandidate().getEmail());
        resp.setCandidatePhone(app.getCandidate().getPhone());

        UserProfile profile = userProfileRepository.findByUserId(app.getCandidate().getId()).orElse(null);
        if (profile != null) {
            resp.setCandidateHeadline(profile.getHeadline());
            resp.setCandidateSkills(profile.getSkills());
            resp.setCandidateLocation(profile.getLocation());
        }

        resp.setCoverLetter(app.getCoverLetter());
        resp.setResumeUrl(app.getResumeUrl());
        resp.setStatus(app.getStatus());
        resp.setRecruiterNotes(app.getRecruiterNotes());
        resp.setAppliedAt(app.getAppliedAt());
        resp.setUpdatedAt(app.getUpdatedAt());

        return resp;
    }
}
