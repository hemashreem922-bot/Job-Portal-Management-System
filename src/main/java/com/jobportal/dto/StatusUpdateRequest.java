package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;

public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // "APPLIED", "IN_REVIEW", "SHORTLISTED", "INTERVIEW", "ACCEPTED", "REJECTED"

    private String recruiterNotes;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status, String recruiterNotes) {
        this.status = status;
        this.recruiterNotes = recruiterNotes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecruiterNotes() {
        return recruiterNotes;
    }

    public void setRecruiterNotes(String recruiterNotes) {
        this.recruiterNotes = recruiterNotes;
    }
}
