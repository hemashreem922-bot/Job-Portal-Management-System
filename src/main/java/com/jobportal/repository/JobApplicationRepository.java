package com.jobportal.repository;

import com.jobportal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);

    List<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId);

    @Query("SELECT a FROM JobApplication a WHERE a.job.employer.id = :employerId ORDER BY a.appliedAt DESC")
    List<JobApplication> findByEmployerIdOrderByAppliedAtDesc(@Param("employerId") Long employerId);

    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);

    long countByJobId(Long jobId);

    long countByStatus(String status);
}
