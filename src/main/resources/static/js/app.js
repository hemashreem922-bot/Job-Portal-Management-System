/**
 * Job Portal Management System - Client Application
 */

const API_BASE = '/api';

const state = {
    currentUser: null,
    activeTab: 'jobs',
    allJobs: [],
    selectedJob: null,
    currentCategory: '',
    searchFilters: {
        keyword: '',
        category: '',
        jobType: '',
        location: ''
    }
};

// ==========================================
// Initialization & Authentication
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // Check saved session or default to Alice for immediate interactive preview
    const savedUser = localStorage.getItem('jobPortalUser');
    if (savedUser) {
        try {
            state.currentUser = JSON.parse(savedUser);
        } catch (e) {
            state.currentUser = null;
        }
    } else {
        // Quick default to Candidate Alice Johnson
        state.currentUser = {
            id: 3,
            email: 'alice@candidate.com',
            fullName: 'Alice Johnson',
            role: 'JOB_SEEKER'
        };
        localStorage.setItem('jobPortalUser', JSON.stringify(state.currentUser));
    }

    updateNavbarUser();
    setupEventListeners();
    loadJobs();
});

function updateNavbarUser() {
    const userRoleBadge = document.getElementById('userRoleBadge');
    const userNameSpan = document.getElementById('userNameSpan');
    const authActions = document.getElementById('authActions');
    const userProfileDropdown = document.getElementById('userProfileDropdown');
    const seekerLinks = document.querySelectorAll('.seeker-only');
    const employerLinks = document.querySelectorAll('.employer-only');
    const adminLinks = document.querySelectorAll('.admin-only');

    if (state.currentUser) {
        authActions.classList.add('d-none');
        userProfileDropdown.classList.remove('d-none');
        userNameSpan.textContent = state.currentUser.fullName;

        let badgeClass = 'bg-primary';
        let roleDisplay = 'Job Seeker';
        if (state.currentUser.role === 'EMPLOYER') {
            badgeClass = 'bg-purple text-white';
            roleDisplay = state.currentUser.companyName || 'Employer';
        } else if (state.currentUser.role === 'ADMIN') {
            badgeClass = 'bg-danger text-white';
            roleDisplay = 'Administrator';
        }

        userRoleBadge.className = `badge rounded-pill ${badgeClass}`;
        userRoleBadge.textContent = roleDisplay;

        // Toggle role nav links
        seekerLinks.forEach(el => el.classList.toggle('d-none', state.currentUser.role !== 'JOB_SEEKER'));
        employerLinks.forEach(el => el.classList.toggle('d-none', state.currentUser.role !== 'EMPLOYER'));
        adminLinks.forEach(el => el.classList.toggle('d-none', state.currentUser.role !== 'ADMIN'));
    } else {
        authActions.classList.remove('d-none');
        userProfileDropdown.classList.add('d-none');
        seekerLinks.forEach(el => el.classList.add('d-none'));
        employerLinks.forEach(el => el.classList.add('d-none'));
        adminLinks.forEach(el => el.classList.add('d-none'));
    }
}

// 1-Click Quick Demo Login
async function quickLogin(role) {
    let email = 'alice@candidate.com';
    let password = 'candidate123';

    if (role === 'EMPLOYER') {
        email = 'recruiter@techcorp.com';
        password = 'recruiter123';
    } else if (role === 'ADMIN') {
        email = 'admin@jobportal.com';
        password = 'admin123';
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Login failed');

        state.currentUser = {
            id: data.id,
            email: data.email,
            fullName: data.fullName,
            role: data.role,
            companyName: data.companyName,
            token: data.token
        };
        localStorage.setItem('jobPortalUser', JSON.stringify(state.currentUser));
        updateNavbarUser();
        showToast(`Logged in as ${data.fullName} (${data.role})`, 'success');

        // Route to natural starting tab
        if (role === 'EMPLOYER') {
            switchTab('employer-dashboard');
        } else if (role === 'ADMIN') {
            switchTab('admin-dashboard');
        } else {
            switchTab('jobs');
            loadJobs();
        }
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

function logout() {
    state.currentUser = null;
    localStorage.removeItem('jobPortalUser');
    updateNavbarUser();
    switchTab('jobs');
    loadJobs();
    showToast('Logged out successfully', 'info');
}

// ==========================================
// Navigation & Tab Switching
// ==========================================
function switchTab(tabName) {
    state.activeTab = tabName;

    // Nav active states
    document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
        link.classList.toggle('active', link.dataset.tab === tabName);
    });

    // Hide/show views
    document.getElementById('viewJobs').classList.toggle('d-none', tabName !== 'jobs');
    document.getElementById('viewApplications').classList.toggle('d-none', tabName !== 'my-applications');
    document.getElementById('viewSavedJobs').classList.toggle('d-none', tabName !== 'saved-jobs');
    document.getElementById('viewEmployer').classList.toggle('d-none', tabName !== 'employer-dashboard');
    document.getElementById('viewAdmin').classList.toggle('d-none', tabName !== 'admin-dashboard');

    // Hero banner display
    const heroSection = document.getElementById('heroSection');
    const searchCard = document.getElementById('searchCard');
    if (tabName === 'jobs') {
        heroSection.classList.remove('d-none');
        searchCard.classList.remove('d-none');
    } else {
        heroSection.classList.add('d-none');
        searchCard.classList.add('d-none');
    }

    if (tabName === 'jobs') loadJobs();
    if (tabName === 'my-applications') loadMyApplications();
    if (tabName === 'saved-jobs') loadSavedJobs();
    if (tabName === 'employer-dashboard') loadEmployerDashboard();
    if (tabName === 'admin-dashboard') loadAdminDashboard();

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ==========================================
// Job Search & Explore View
// ==========================================
async function loadJobs() {
    const jobsListContainer = document.getElementById('jobsListContainer');
    const jobsCountBadge = document.getElementById('jobsCountBadge');
    jobsListContainer.innerHTML = '<div class="col-12 text-center py-5"><div class="spinner-border text-primary" role="status"></div><p class="mt-2 text-muted">Searching available jobs...</p></div>';

    const params = new URLSearchParams();
    if (state.searchFilters.keyword) params.append('keyword', state.searchFilters.keyword);
    if (state.searchFilters.category) params.append('category', state.searchFilters.category);
    if (state.searchFilters.jobType) params.append('jobType', state.searchFilters.jobType);
    if (state.searchFilters.location) params.append('location', state.searchFilters.location);
    if (state.currentUser) params.append('userId', state.currentUser.id);

    try {
        const res = await fetch(`${API_BASE}/jobs?${params.toString()}`);
        const jobs = await res.json();
        state.allJobs = jobs;

        jobsCountBadge.textContent = `${jobs.length} Jobs Found`;

        if (!jobs || jobs.length === 0) {
            jobsListContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <div class="mb-3 text-muted" style="font-size: 3rem;"><i class="fa-solid fa-briefcase"></i></div>
                    <h5>No job listings found</h5>
                    <p class="text-muted">Try tweaking your keyword, category, or location filters.</p>
                    <button class="btn btn-outline-primary btn-sm" onclick="resetSearchFilters()">Clear All Filters</button>
                </div>
            `;
            return;
        }

        jobsListContainer.innerHTML = jobs.map(job => renderJobCard(job)).join('');
    } catch (err) {
        jobsListContainer.innerHTML = `<div class="col-12 text-center py-5 text-danger"><i class="fa-solid fa-circle-exclamation me-2"></i>Failed to load jobs: ${err.message}</div>`;
    }
}

function renderJobCard(job) {
    const salaryText = (job.salaryMin && job.salaryMax)
        ? `$${Math.round(job.salaryMin / 1000)}k - $${Math.round(job.salaryMax / 1000)}k`
        : 'Competitive Salary';

    const companyLogo = job.companyLogo
        ? `<img src="${job.companyLogo}" alt="${job.companyName}" class="company-logo">`
        : `<div class="company-avatar-fallback">${job.companyName ? job.companyName.charAt(0) : 'J'}</div>`;

    const savedIconClass = job.saved ? 'fa-solid text-danger' : 'fa-regular text-muted';
    const applyButtonState = job.hasApplied
        ? `<span class="badge bg-success-subtle text-success py-2 px-3 fw-bold rounded-pill"><i class="fa-solid fa-check me-1"></i>Applied</span>`
        : `<button class="btn btn-sm btn-primary" onclick="openJobDetails(${job.id}, true)">Apply Now</button>`;

    return `
        <div class="col-lg-6 col-md-12 mb-4">
            <div class="job-card">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <div class="d-flex align-items-center gap-3">
                        ${companyLogo}
                        <div>
                            <h5 class="mb-1"><a href="javascript:void(0)" class="job-title" onclick="openJobDetails(${job.id})">${escapeHtml(job.title)}</a></h5>
                            <div class="company-name">${escapeHtml(job.companyName || 'Company')}</div>
                        </div>
                    </div>
                    ${state.currentUser && state.currentUser.role === 'JOB_SEEKER' ? `
                        <button class="btn btn-link p-1 text-decoration-none" title="Save Job" onclick="toggleSaveJob(${job.id})">
                            <i class="${savedIconClass} fa-bookmark fs-5"></i>
                        </button>
                    ` : ''}
                </div>

                <p class="text-muted small mb-3 flex-grow-1" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                    ${escapeHtml(job.description)}
                </p>

                <div class="d-flex flex-wrap gap-2 mb-3">
                    <span class="job-tag tag-type"><i class="fa-regular fa-clock"></i> ${escapeHtml(job.jobType)}</span>
                    <span class="job-tag tag-cat"><i class="fa-solid fa-layer-group"></i> ${escapeHtml(job.category)}</span>
                    <span class="job-tag tag-loc"><i class="fa-solid fa-location-dot"></i> ${escapeHtml(job.location)}</span>
                    <span class="job-tag tag-salary"><i class="fa-solid fa-money-bill-wave"></i> ${salaryText}</span>
                </div>

                <div class="d-flex justify-content-between align-items-center pt-3 border-top mt-auto">
                    <span class="text-muted small"><i class="fa-solid fa-users me-1"></i>${job.applicantCount} applicants</span>
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-outline-secondary" onclick="openJobDetails(${job.id})">View Details</button>
                        ${applyButtonState}
                    </div>
                </div>
            </div>
        </div>
    `;
}

// ==========================================
// Job Details Modal & Apply Workflow
// ==========================================
async function openJobDetails(jobId, focusApply = false) {
    let job = state.allJobs.find(j => j.id === jobId) || null;

    try {
        const userIdParam = state.currentUser ? `?userId=${state.currentUser.id}` : '';
        const res = await fetch(`${API_BASE}/jobs/${jobId}${userIdParam}`);
        if (res.ok) {
            job = await res.json();
        } else if (!job) {
            const errData = await res.json().catch(() => ({}));
            throw new Error(errData.error || 'Job not found');
        }
    } catch (err) {
        if (!job) {
            const isOffline = err.name === 'TypeError' || err.message.includes('fetch');
            const errorMsg = isOffline 
                ? 'Backend server is not reachable on port 8080. Please ensure the Java Spring Boot backend is running.'
                : err.message;
            showToast(errorMsg, 'danger');
            return;
        }
    }

    if (!job) return;
    state.selectedJob = job;

    document.getElementById('modalJobTitle').textContent = job.title;
    document.getElementById('modalCompanyName').textContent = job.companyName || 'Company';
    document.getElementById('modalJobLocation').textContent = job.location || 'Remote';
    document.getElementById('modalJobType').textContent = job.jobType || 'Full-time';
    document.getElementById('modalJobCategory').textContent = job.category || 'General';
    document.getElementById('modalJobExperience').textContent = job.experienceLevel || 'Not specified';
    
    const salaryText = (job.salaryMin && job.salaryMax)
        ? `$${job.salaryMin.toLocaleString()} - $${job.salaryMax.toLocaleString()} / year`
        : 'Competitive Salary';
    document.getElementById('modalJobSalary').textContent = salaryText;

    document.getElementById('modalJobDescription').innerHTML = escapeHtml(job.description || '').replace(/\n/g, '<br>');
    document.getElementById('modalJobRequirements').innerHTML = escapeHtml(job.requirements || 'None specified').replace(/\n/g, '<br>');
    document.getElementById('modalJobBenefits').innerHTML = escapeHtml(job.benefits || 'Standard benefits apply').replace(/\n/g, '<br>');
    document.getElementById('modalJobDeadline').textContent = job.deadline ? `Deadline: ${job.deadline}` : 'Open until filled';

    // Apply Section in Modal
    const applySection = document.getElementById('modalApplySection');
    if (state.currentUser && state.currentUser.role === 'JOB_SEEKER') {
        if (job.hasApplied) {
            applySection.innerHTML = `
                <div class="alert alert-success d-flex align-items-center gap-2 mb-0">
                    <i class="fa-solid fa-circle-check fs-5"></i>
                    <div>You have applied for this position. View status in <strong>My Applications</strong>.</div>
                </div>
            `;
        } else {
            applySection.innerHTML = `
                <div class="card bg-light border-0 p-3">
                    <h6 class="fw-bold mb-2"><i class="fa-solid fa-paper-plane text-primary me-2"></i>Apply for this position</h6>
                    <div class="mb-2">
                        <label class="form-label small fw-semibold">Cover Letter / Pitch (Optional)</label>
                        <textarea id="applyCoverLetter" class="form-control form-control-sm" rows="3" placeholder="Briefly highlight why your background fits this role..."></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold">Resume Link / Portfolio</label>
                        <input type="text" id="applyResumeUrl" class="form-control form-control-sm" placeholder="https://example.com/my-resume.pdf">
                    </div>
                    <button class="btn btn-primary" onclick="submitApplication(${job.id})">Submit Application</button>
                </div>
            `;
        }
    } else if (!state.currentUser) {
        applySection.innerHTML = `
            <div class="alert alert-info mb-0">
                Please <a href="javascript:void(0)" onclick="quickLogin('JOB_SEEKER')" class="alert-link fw-bold">Sign In as Candidate</a> to apply for this job.
            </div>
        `;
    } else {
        applySection.innerHTML = '';
    }

    const modal = new bootstrap.Modal(document.getElementById('jobDetailsModal'));
    modal.show();

    if (focusApply) {
        setTimeout(() => {
            const el = document.getElementById('applyCoverLetter');
            if (el) el.focus();
        }, 500);
    }
}

async function submitApplication(jobId) {
    if (!state.currentUser) {
        showToast('Please login as a candidate first', 'warning');
        return;
    }

    const coverLetter = document.getElementById('applyCoverLetter')?.value || '';
    const resumeUrl = document.getElementById('applyResumeUrl')?.value || '';

    try {
        const res = await fetch(`${API_BASE}/applications?candidateId=${state.currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ jobId, coverLetter, resumeUrl })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to submit application');

        showToast('Application successfully submitted! 🎉', 'success');
        bootstrap.Modal.getInstance(document.getElementById('jobDetailsModal')).hide();
        loadJobs();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

async function toggleSaveJob(jobId) {
    if (!state.currentUser) {
        showToast('Please sign in to save jobs', 'info');
        return;
    }
    try {
        const res = await fetch(`${API_BASE}/jobs/${jobId}/save?userId=${state.currentUser.id}`, {
            method: 'POST'
        });
        const data = await res.json();
        showToast(data.message, 'info');
        loadJobs();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

// ==========================================
// Candidate Applications Dashboard
// ==========================================
async function loadMyApplications() {
    if (!state.currentUser) return;
    const container = document.getElementById('myApplicationsTableBody');
    container.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm me-2"></div>Loading applications...</td></tr>';

    try {
        const res = await fetch(`${API_BASE}/applications/my-applications?candidateId=${state.currentUser.id}`);
        const apps = await res.json();

        if (!apps || apps.length === 0) {
            container.innerHTML = `<tr><td colspan="6" class="text-center py-5 text-muted">You haven't submitted any job applications yet.<br><button class="btn btn-sm btn-primary mt-2" onclick="switchTab('jobs')">Browse Open Jobs</button></td></tr>`;
            return;
        }

        container.innerHTML = apps.map(app => {
            const statusClass = `status-${app.status.toLowerCase()}`;
            const formattedDate = new Date(app.appliedAt).toLocaleDateString();

            return `
                <tr>
                    <td class="fw-bold">${escapeHtml(app.jobTitle)}</td>
                    <td>${escapeHtml(app.companyName)}</td>
                    <td>${escapeHtml(app.jobLocation)}</td>
                    <td>${formattedDate}</td>
                    <td><span class="status-badge ${statusClass}">${escapeHtml(app.status)}</span></td>
                    <td>
                        <span class="small text-muted">${escapeHtml(app.recruiterNotes || 'Application submitted')}</span>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        container.innerHTML = `<tr><td colspan="6" class="text-center text-danger py-4">Error loading applications: ${err.message}</td></tr>`;
    }
}

async function loadSavedJobs() {
    if (!state.currentUser) return;
    const container = document.getElementById('savedJobsContainer');
    container.innerHTML = '<div class="col-12 text-center py-4 text-muted"><div class="spinner-border spinner-border-sm"></div> Loading saved jobs...</div>';

    try {
        const res = await fetch(`${API_BASE}/jobs/saved?userId=${state.currentUser.id}`);
        const jobs = await res.json();

        if (!jobs || jobs.length === 0) {
            container.innerHTML = `
                <div class="col-12 text-center py-5 text-muted">
                    <i class="fa-regular fa-bookmark fs-1 mb-2"></i>
                    <h5>No saved jobs</h5>
                    <p>Click the bookmark icon on any job card to save it for later.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = jobs.map(job => renderJobCard(job)).join('');
    } catch (err) {
        container.innerHTML = `<div class="col-12 text-danger text-center">Failed to load saved jobs: ${err.message}</div>`;
    }
}

// ==========================================
// Employer Dashboard & Applicant Pipeline
// ==========================================
async function loadEmployerDashboard() {
    if (!state.currentUser) return;
    const employerId = state.currentUser.id;

    try {
        // 1. Fetch Employer Jobs
        const jobsRes = await fetch(`${API_BASE}/jobs/employer/${employerId}`);
        const jobs = await jobsRes.json();

        // 2. Fetch Received Applications
        const appsRes = await fetch(`${API_BASE}/applications/employer/${employerId}`);
        const apps = await appsRes.json();

        // Stats Counters
        document.getElementById('empActiveJobsCount').textContent = jobs.filter(j => j.status === 'ACTIVE').length;
        document.getElementById('empTotalApplicantsCount').textContent = apps.length;
        document.getElementById('empShortlistedCount').textContent = apps.filter(a => a.status === 'SHORTLISTED' || a.status === 'INTERVIEW').length;

        // Render Posted Jobs Table
        const jobsTbody = document.getElementById('employerJobsTableBody');
        if (!jobs || jobs.length === 0) {
            jobsTbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No jobs posted yet. Click <strong>+ Post a Job</strong> to publish your first opening!</td></tr>`;
        } else {
            jobsTbody.innerHTML = jobs.map(j => `
                <tr>
                    <td class="fw-bold">${escapeHtml(j.title)}</td>
                    <td>${escapeHtml(j.category)}</td>
                    <td><span class="badge ${j.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${j.status}</span></td>
                    <td>${j.applicantCount} applicants</td>
                    <td>${new Date(j.createdAt).toLocaleDateString()}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteJob(${j.id})"><i class="fa-solid fa-trash-can"></i></button>
                    </td>
                </tr>
            `).join('');
        }

        // Render Applicant Review Pipeline
        const appsTbody = document.getElementById('employerApplicantsTableBody');
        if (!apps || apps.length === 0) {
            appsTbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No candidates have applied yet.</td></tr>`;
        } else {
            appsTbody.innerHTML = apps.map(app => `
                <tr>
                    <td>
                        <div class="fw-bold">${escapeHtml(app.candidateName)}</div>
                        <div class="small text-muted">${escapeHtml(app.candidateEmail)}</div>
                    </td>
                    <td>${escapeHtml(app.jobTitle)}</td>
                    <td>
                        <div class="small fw-semibold">${escapeHtml(app.candidateHeadline || 'Candidate')}</div>
                        <div class="small text-muted" style="max-width: 200px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                            ${escapeHtml(app.candidateSkills || 'N/A')}
                        </div>
                    </td>
                    <td>
                        ${app.resumeUrl ? `<a href="${app.resumeUrl}" target="_blank" class="btn btn-sm btn-outline-primary"><i class="fa-solid fa-file-pdf me-1"></i>Resume</a>` : '<span class="text-muted small">None</span>'}
                    </td>
                    <td>
                        <select class="form-select form-select-sm" onchange="updateApplicantStatus(${app.id}, this.value)">
                            <option value="APPLIED" ${app.status === 'APPLIED' ? 'selected' : ''}>Applied</option>
                            <option value="IN_REVIEW" ${app.status === 'IN_REVIEW' ? 'selected' : ''}>In Review</option>
                            <option value="SHORTLISTED" ${app.status === 'SHORTLISTED' ? 'selected' : ''}>Shortlisted</option>
                            <option value="INTERVIEW" ${app.status === 'INTERVIEW' ? 'selected' : ''}>Interview Scheduled</option>
                            <option value="ACCEPTED" ${app.status === 'ACCEPTED' ? 'selected' : ''}>Accepted</option>
                            <option value="REJECTED" ${app.status === 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        </select>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-outline-secondary" onclick="openNotesModal(${app.id}, '${escapeHtml(app.candidateName)}', '${escapeHtml(app.recruiterNotes || '')}')">
                            <i class="fa-regular fa-comment-dots"></i> Notes
                        </button>
                    </td>
                </tr>
            `).join('');
        }

    } catch (err) {
        showToast(`Failed to load employer dashboard: ${err.message}`, 'danger');
    }
}

async function updateApplicantStatus(applicationId, newStatus) {
    try {
        const res = await fetch(`${API_BASE}/applications/${applicationId}/status?userId=${state.currentUser.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to update status');

        showToast(`Candidate status updated to ${newStatus}`, 'success');
        loadEmployerDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

function openNotesModal(appId, candidateName, currentNotes) {
    document.getElementById('notesModalAppId').value = appId;
    document.getElementById('notesModalCandidateName').textContent = candidateName;
    document.getElementById('notesModalText').value = currentNotes;
    new bootstrap.Modal(document.getElementById('recruiterNotesModal')).show();
}

async function saveRecruiterNotes() {
    const appId = document.getElementById('notesModalAppId').value;
    const notes = document.getElementById('notesModalText').value;

    try {
        const res = await fetch(`${API_BASE}/applications/${appId}/status?userId=${state.currentUser.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'IN_REVIEW', recruiterNotes: notes })
        });
        if (!res.ok) throw new Error('Failed to save recruiter notes');

        bootstrap.Modal.getInstance(document.getElementById('recruiterNotesModal')).hide();
        showToast('Notes saved successfully', 'success');
        loadEmployerDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

function openPostJobModal() {
    document.getElementById('postJobForm').reset();
    new bootstrap.Modal(document.getElementById('postJobModal')).show();
}

async function handlePostJobSubmit(e) {
    e.preventDefault();
    if (!state.currentUser) return;

    const payload = {
        title: document.getElementById('jobPostTitle').value,
        category: document.getElementById('jobPostCategory').value,
        jobType: document.getElementById('jobPostType').value,
        location: document.getElementById('jobPostLocation').value,
        salaryMin: parseFloat(document.getElementById('jobPostSalaryMin').value) || null,
        salaryMax: parseFloat(document.getElementById('jobPostSalaryMax').value) || null,
        experienceLevel: document.getElementById('jobPostExpLevel').value,
        description: document.getElementById('jobPostDescription').value,
        requirements: document.getElementById('jobPostRequirements').value,
        benefits: document.getElementById('jobPostBenefits').value,
        deadline: document.getElementById('jobPostDeadline').value || null,
        status: 'ACTIVE'
    };

    try {
        const res = await fetch(`${API_BASE}/jobs?employerId=${state.currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to post job');

        bootstrap.Modal.getInstance(document.getElementById('postJobModal')).hide();
        showToast('Job opening published successfully! 🚀', 'success');
        loadEmployerDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

async function deleteJob(jobId) {
    if (!confirm('Are you sure you want to delete this job listing?')) return;
    try {
        const res = await fetch(`${API_BASE}/jobs/${jobId}?userId=${state.currentUser.id}`, {
            method: 'DELETE'
        });
        if (!res.ok) throw new Error('Failed to delete job');
        showToast('Job listing removed', 'info');
        loadEmployerDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

// ==========================================
// Admin Dashboard
// ==========================================
async function loadAdminDashboard() {
    try {
        const statsRes = await fetch(`${API_BASE}/admin/stats`);
        const stats = await statsRes.json();

        document.getElementById('adminTotalUsers').textContent = stats.totalUsers;
        document.getElementById('adminTotalJobs').textContent = stats.totalJobs;
        document.getElementById('adminTotalApps').textContent = stats.totalApplications;
        document.getElementById('adminShortlistedApps').textContent = stats.shortlistedApplications;

        // Load users list
        const usersRes = await fetch(`${API_BASE}/admin/users`);
        const users = await usersRes.json();

        const usersTbody = document.getElementById('adminUsersTableBody');
        usersTbody.innerHTML = users.map(u => `
            <tr>
                <td>#${u.id}</td>
                <td class="fw-bold">${escapeHtml(u.fullName)}</td>
                <td>${escapeHtml(u.email)}</td>
                <td><span class="badge ${u.role === 'ADMIN' ? 'bg-danger' : (u.role === 'EMPLOYER' ? 'bg-primary' : 'bg-info')}">${u.role}</span></td>
                <td>
                    <span class="badge ${u.active ? 'bg-success' : 'bg-secondary'}">${u.active ? 'Active' : 'Disabled'}</span>
                </td>
                <td>
                    <button class="btn btn-sm ${u.active ? 'btn-outline-danger' : 'btn-outline-success'}" onclick="toggleUserActive(${u.id})">
                        ${u.active ? 'Disable' : 'Enable'}
                    </button>
                </td>
            </tr>
        `).join('');

    } catch (err) {
        showToast(`Failed to load admin stats: ${err.message}`, 'danger');
    }
}

async function toggleUserActive(userId) {
    try {
        const res = await fetch(`${API_BASE}/admin/users/${userId}/toggle-status`, {
            method: 'PUT'
        });
        const data = await res.json();
        showToast(data.message, 'info');
        loadAdminDashboard();
    } catch (err) {
        showToast(err.message, 'danger');
    }
}

// ==========================================
// Search & Filter Listeners
// ==========================================
function setupEventListeners() {
    // Search form
    const searchForm = document.getElementById('jobSearchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            state.searchFilters.keyword = document.getElementById('searchKeyword').value;
            state.searchFilters.category = document.getElementById('searchCategory').value;
            state.searchFilters.jobType = document.getElementById('searchJobType').value;
            state.searchFilters.location = document.getElementById('searchLocation').value;
            loadJobs();
        });
    }

    // Category Chips
    document.querySelectorAll('.filter-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            const cat = chip.dataset.category;
            state.searchFilters.category = cat;
            document.getElementById('searchCategory').value = cat;
            loadJobs();
        });
    });

    // Post Job Form
    const postJobForm = document.getElementById('postJobForm');
    if (postJobForm) {
        postJobForm.addEventListener('submit', handlePostJobSubmit);
    }
}

function resetSearchFilters() {
    document.getElementById('searchKeyword').value = '';
    document.getElementById('searchCategory').value = '';
    document.getElementById('searchJobType').value = '';
    document.getElementById('searchLocation').value = '';

    document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
    document.querySelector('.filter-chip[data-category=""]').classList.add('active');

    state.searchFilters = { keyword: '', category: '', jobType: '', location: '' };
    loadJobs();
}

// ==========================================
// Toast Helpers
// ==========================================
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-bg-${type} border-0 show mb-2 shadow`;
    toastEl.role = 'alert';
    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${escapeHtml(message)}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    container.appendChild(toastEl);
    setTimeout(() => {
        toastEl.remove();
    }, 4000);
}

function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
