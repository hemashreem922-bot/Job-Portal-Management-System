package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.model.*;
import com.jobportal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository,
                      CompanyRepository companyRepository,
                      JobApplicationRepository applicationRepository,
                      SavedJobRepository savedJobRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.savedJobRepository = savedJobRepository;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(String keyword, String category, String jobType, String location, Long currentUserId) {
        List<Job> jobs = jobRepository.searchActiveJobs(
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                (category != null && !category.isBlank()) ? category.trim() : null,
                (jobType != null && !jobType.isBlank()) ? jobType.trim() : null,
                (location != null && !location.isBlank()) ? location.trim() : null
        );

        return jobs.stream()
                .map(job -> toJobResponse(job, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobsAdmin() {
        return jobRepository.findAll().stream()
                .map(job -> toJobResponse(job, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + jobId));
        return toJobResponse(job, currentUserId);
    }

    @Transactional
    public JobResponse createJob(JobRequest request, Long employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer not found"));

        if (employer.getRole() != Role.EMPLOYER && employer.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only employers or admins can post jobs");
        }

        Job job = new Job();
        job.setEmployer(employer);
        job.setTitle(request.getTitle().trim());
        job.setCategory(request.getCategory().trim());
        job.setJobType(request.getJobType().trim());
        job.setLocation(request.getLocation().trim());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setDescription(request.getDescription().trim());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setDeadline(request.getDeadline());
        job.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        job.setCreatedAt(LocalDateTime.now());

        job = jobRepository.save(job);
        return toJobResponse(job, employerId);
    }

    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request, Long employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + jobId));

        User currentUser = userRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != Role.ADMIN && !job.getEmployer().getId().equals(employerId)) {
            throw new IllegalStateException("You are not authorized to update this job");
        }

        job.setTitle(request.getTitle().trim());
        job.setCategory(request.getCategory().trim());
        job.setJobType(request.getJobType().trim());
        job.setLocation(request.getLocation().trim());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setDescription(request.getDescription().trim());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setDeadline(request.getDeadline());
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        job = jobRepository.save(job);
        return toJobResponse(job, employerId);
    }

    @Transactional
    public void deleteJob(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + jobId));

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != Role.ADMIN && !job.getEmployer().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to delete this job");
        }

        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getEmployerJobs(Long employerId) {
        return jobRepository.findByEmployerIdOrderByCreatedAtDesc(employerId).stream()
                .map(job -> toJobResponse(job, employerId))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleSaveJob(Long jobId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        if (savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            savedJobRepository.deleteByUserIdAndJobId(userId, jobId);
            return false;
        } else {
            SavedJob savedJob = new SavedJob(user, job);
            savedJobRepository.save(savedJob);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getSavedJobs(Long userId) {
        List<SavedJob> savedJobs = savedJobRepository.findByUserIdOrderBySavedAtDesc(userId);
        return savedJobs.stream()
                .map(sj -> toJobResponse(sj.getJob(), userId))
                .collect(Collectors.toList());
    }

    public JobResponse toJobResponse(Job job, Long currentUserId) {
        JobResponse resp = new JobResponse();
        resp.setId(job.getId());
        resp.setEmployerId(job.getEmployer().getId());
        resp.setEmployerName(job.getEmployer().getFullName());

        Company company = companyRepository.findByUserId(job.getEmployer().getId()).orElse(null);
        if (company != null) {
            resp.setCompanyName(company.getCompanyName());
            resp.setCompanyLogo(company.getLogoUrl());
            resp.setCompanyWebsite(company.getWebsite());
            resp.setCompanyLocation(company.getLocation());
        } else {
            resp.setCompanyName(job.getEmployer().getFullName());
        }

        resp.setTitle(job.getTitle());
        resp.setCategory(job.getCategory());
        resp.setJobType(job.getJobType());
        resp.setLocation(job.getLocation());
        resp.setSalaryMin(job.getSalaryMin());
        resp.setSalaryMax(job.getSalaryMax());
        resp.setExperienceLevel(job.getExperienceLevel());
        resp.setDescription(job.getDescription());
        resp.setRequirements(job.getRequirements());
        resp.setBenefits(job.getBenefits());
        resp.setDeadline(job.getDeadline());
        resp.setStatus(job.getStatus());
        resp.setCreatedAt(job.getCreatedAt());

        resp.setApplicantCount(applicationRepository.countByJobId(job.getId()));

        if (currentUserId != null) {
            resp.setSaved(savedJobRepository.existsByUserIdAndJobId(currentUserId, job.getId()));
            resp.setHasApplied(applicationRepository.existsByJobIdAndCandidateId(job.getId(), currentUserId));
        }

        return resp;
    }
}
