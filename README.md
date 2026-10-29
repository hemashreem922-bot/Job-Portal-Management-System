# Job Portal Management System

A full-stack, enterprise-grade **Job Portal Management System** built with **Java (Spring Boot 4)** and a **Relational SQL Database** (with dual support for MySQL and zero-configuration embedded H2 SQL database), complete with a responsive, modern web interface.

---

## 🌟 Key Features

### 1. Job Seeker (Candidate) Portal
- **Browse & Search Jobs**: Live keyword search across titles, descriptions, and skills.
- **Smart Filtering**: Filter by category (*Software Engineering*, *DevOps & Cloud*, *Data & AI*, *Product & Design*), job type (*Full-time*, *Part-time*, *Remote*, *Contract*, *Internship*), and location.
- **Job Details Modal**: Full job descriptions, qualifications, requirements, compensation, and perks.
- **Easy Apply**: Instant 1-click apply modal with cover letter notes and resume link.
- **Application Tracking**: Real-time status tracking (*APPLIED*, *IN_REVIEW*, *SHORTLISTED*, *INTERVIEW*, *ACCEPTED*, *REJECTED*) with recruiter feedback notes.
- **Bookmark & Save**: Bookmark jobs for later viewing.

### 2. Employer (Recruiter) Portal
- **Company Profile**: Manage company information, logo, website, and industry focus.
- **Post & Manage Jobs**: Rich form for posting openings with compensation ranges, qualifications, and deadlines.
- **Applicant Review Pipeline**: Inspect incoming applications, candidate headline, skills, resume, and cover letters.
- **Status Progression & Notes**: Move candidates through hiring stages with custom recruiter feedback notes.

### 3. Administrator Portal
- **System Overview**: Metrics for Total Users, Active Jobs, Total Applications, and Pipeline Activity.
- **User Moderation**: Manage candidate and recruiter accounts (activate / deactivate).
- **Database Console Access**: Direct web access to the SQL database console at `/h2-console`.

---

## 🏗️ Technology Stack

- **Backend**: Java 21 / 26, Spring Boot 4.x
  - Spring Web MVC (REST API)
  - Spring Data JPA / Hibernate
  - Jakarta Bean Validation
- **Database**:
  - **Default**: Relational H2 Database with file persistence (`./data/jobportaldb`)
  - **Production/Alternative**: MySQL Database (`application-mysql.properties`)
- **Frontend**:
  - HTML5, CSS3, JavaScript (ES6 SPA)
  - Bootstrap 5.3 & FontAwesome 6
  - Served directly by Spring Boot (no Node/NPM required)

---

## 🚀 Quick Start (Running the Application)

### Prerequisites
- Java JDK installed (Java 21 or newer).

### Method 1: Using the 1-Click Batch Script (Windows)
Double-click `run.bat` or run:
```cmd
run.bat
```

### Method 2: Using PowerShell
```powershell
.\run.ps1
```

### Method 3: Using the Maven Wrapper Directly
```bash
.\mvnw.cmd spring-boot:run
```

Once started, open your web browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 👥 Default Demo Accounts

The system automatically initializes with realistic sample users, job postings, and applications:

| Role | Email | Password | Full Name / Organization |
| :--- | :--- | :--- | :--- |
| **Candidate** | `alice@candidate.com` | `candidate123` | Alice Johnson (Senior Java Developer) |
| **Candidate** | `bob@candidate.com` | `candidate123` | Bob Williams (DevOps & Platform Eng.) |
| **Employer** | `recruiter@techcorp.com` | `recruiter123` | Sarah Jenkins (TechCorp Global) |
| **Employer** | `hr@innovatelabs.io` | `recruiter123` | David Miller (Innovate Labs) |
| **Admin** | `admin@jobportal.com` | `admin123` | System Administrator |

> 💡 **Tip**: In the top navigation bar, click the **Role** pills (*Candidate*, *Employer*, *Admin*) for instant 1-click demo login!

---

## 🗄️ Relational SQL Database

### Default: H2 SQL Database (File-Persistent)
- The database is persisted to the local directory: `./data/jobportaldb.mv.db`.
- **H2 Web Console**: Access the interactive database console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console).
  - **JDBC URL**: `jdbc:h2:file:./data/jobportaldb`
  - **Username**: `sa`
  - **Password**: `password`

### Switching to MySQL
To use MySQL instead of H2:
1. Make sure your MySQL server is running on `localhost:3306`.
2. Update database credentials in `src/main/resources/application-mysql.properties` if needed.
3. Start the application with the MySQL profile:
   ```cmd
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
4. The schema (`src/main/resources/schema.sql`) and tables will automatically be created in `job_portal_db`.

---

## 📡 REST API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user (`JOB_SEEKER` or `EMPLOYER`) |
| `POST` | `/api/auth/login` | Authenticate and get user details |
| `GET` | `/api/jobs` | Search & filter jobs (`keyword`, `category`, `jobType`, `location`) |
| `GET` | `/api/jobs/{id}` | Get specific job details |
| `POST` | `/api/jobs?employerId={id}` | Post a new job listing |
| `PUT` | `/api/jobs/{id}?employerId={id}` | Update existing job |
| `DELETE` | `/api/jobs/{id}?userId={id}` | Remove a job listing |
| `POST` | `/api/jobs/{id}/save?userId={id}` | Toggle bookmarking a job |
| `GET` | `/api/jobs/saved?userId={id}` | Get user's saved jobs |
| `POST` | `/api/applications?candidateId={id}` | Submit an application for a job |
| `GET` | `/api/applications/my-applications?candidateId={id}` | Get candidate's applied jobs and statuses |
| `GET` | `/api/applications/employer/{employerId}` | Get applications received by employer |
| `PUT` | `/api/applications/{id}/status?userId={id}` | Update application status (`SHORTLISTED`, `INTERVIEW`, etc.) |
| `GET` | `/api/admin/stats` | Get system overview metrics |
| `GET` | `/api/admin/users` | List all users |
| `PUT` | `/api/admin/users/{id}/toggle-status` | Enable or disable a user account |

---

## 📁 Project Directory Structure

```
job-portal/
├── .mvn/wrapper/                  # Maven Wrapper binaries & properties
├── data/                          # Persistent H2 SQL database storage
├── src/
│   ├── main/
│   │   ├── java/com/jobportal/
│   │   │   ├── controller/        # REST API controllers
│   │   │   ├── dto/               # Request & response data transfer objects
│   │   │   ├── model/             # JPA entity models (User, Job, Application, etc.)
│   │   │   ├── repository/        # Spring Data JPA repositories
│   │   │   ├── service/           # Business logic & seed data initializer
│   │   │   ├── util/              # Password hashing & utility functions
│   │   │   └── JobPortalApplication.java
│   │   └── resources/
│   │       ├── static/            # Responsive Web UI (HTML5, CSS3, JS SPA)
│   │       ├── application.properties        # Default H2 SQL configuration
│   │       ├── application-mysql.properties  # MySQL configuration profile
│   │       ├── schema.sql         # Standard SQL DDL script
│   │       └── data.sql           # SQL seed script
├── mvnw.cmd                       # Maven Wrapper for Windows
├── pom.xml                        # Maven dependencies & build definitions
├── run.bat                        # 1-Click Windows execution script
├── run.ps1                        # PowerShell execution script
└── README.md                      # Documentation
```
