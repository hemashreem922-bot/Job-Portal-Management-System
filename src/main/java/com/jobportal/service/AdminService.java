package com.jobportal.service;

import com.jobportal.dto.AdminStatsDto;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.model.Job;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ApplicationService applicationService;

    public AdminService(UserRepository userRepository,
                        JobRepository jobRepository,
                        JobApplicationRepository applicationRepository,
                        ApplicationService applicationService) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public AdminStatsDto getStats() {
        AdminStatsDto stats = new AdminStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalCandidates(userRepository.countByRole(Role.JOB_SEEKER));
        stats.setTotalEmployers(userRepository.countByRole(Role.EMPLOYER));

        stats.setTotalJobs(jobRepository.count());
        stats.setActiveJobs(jobRepository.countByStatus("ACTIVE"));
        stats.setClosedJobs(jobRepository.countByStatus("CLOSED"));

        stats.setTotalApplications(applicationRepository.count());
        stats.setPendingApplications(applicationRepository.countByStatus("APPLIED") + applicationRepository.countByStatus("IN_REVIEW"));
        stats.setShortlistedApplications(applicationRepository.countByStatus("SHORTLISTED") + applicationRepository.countByStatus("INTERVIEW"));
        stats.setAcceptedApplications(applicationRepository.countByStatus("ACCEPTED"));
        stats.setRejectedApplications(applicationRepository.countByStatus("REJECTED"));

        List<ApplicationResponse> allApps = applicationService.getAllApplicationsAdmin();
        stats.setRecentApplications(allApps.stream().limit(10).collect(Collectors.toList()));

        return stats;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return user.isActive();
    }

    @Transactional
    public void updateJobStatus(Long jobId, String status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(status.toUpperCase());
        jobRepository.save(job);
    }
}
