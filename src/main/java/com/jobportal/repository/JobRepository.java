package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    List<Job> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR :category = '' OR j.category = :category) " +
           "AND (:jobType IS NULL OR :jobType = '' OR j.jobType = :jobType) " +
           "AND (:location IS NULL OR :location = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "ORDER BY j.createdAt DESC")
    List<Job> searchActiveJobs(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("jobType") String jobType,
            @Param("location") String location
    );

    long countByStatus(String status);
}
