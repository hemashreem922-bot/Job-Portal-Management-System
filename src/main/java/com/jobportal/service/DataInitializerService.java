package com.jobportal.service;

import com.jobportal.model.*;
import com.jobportal.repository.*;
import com.jobportal.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    public DataInitializerService(UserRepository userRepository,
                                  UserProfileRepository userProfileRepository,
                                  CompanyRepository companyRepository,
                                  JobRepository jobRepository,
                                  JobApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Admin
        User admin = new User(
                "admin@jobportal.com",
                PasswordUtil.hashPassword("admin123"),
                "System Administrator",
                Role.ADMIN,
                "+1 (555) 019-2831"
        );
        userRepository.save(admin);

        // 2. Employer 1 - TechCorp
        User emp1 = new User(
                "recruiter@techcorp.com",
                PasswordUtil.hashPassword("recruiter123"),
                "Sarah Jenkins",
                Role.EMPLOYER,
                "+1 (555) 482-9912"
        );
        userRepository.save(emp1);

        Company comp1 = new Company(
                emp1,
                "TechCorp Global",
                "Cloud Software & Enterprise Solutions",
                "https://techcorp.example.com",
                "San Francisco, CA (Hybrid / Remote)",
                "TechCorp is a premier enterprise cloud software firm delivering high-scale microservices, enterprise SaaS, and mission-critical applications globally."
        );
        comp1.setLogoUrl("https://images.unsplash.com/photo-1542744094-3a31f272c490?w=128&auto=format&fit=crop&q=80");
        companyRepository.save(comp1);

        // 3. Employer 2 - Innovate Labs
        User emp2 = new User(
                "hr@innovatelabs.io",
                PasswordUtil.hashPassword("recruiter123"),
                "David Miller",
                Role.EMPLOYER,
                "+1 (555) 391-7720"
        );
        userRepository.save(emp2);

        Company comp2 = new Company(
                emp2,
                "Innovate Labs",
                "FinTech & Distributed Systems",
                "https://innovatelabs.example.com",
                "New York, NY (Remote-First)",
                "Innovate Labs crafts next-generation financial architectures, real-time analytics engines, and resilient trading infrastructure."
        );
        comp2.setLogoUrl("https://images.unsplash.com/photo-1551434678-e076c223a692?w=128&auto=format&fit=crop&q=80");
        companyRepository.save(comp2);

        // 4. Candidate 1 - Alice
        User cand1 = new User(
                "alice@candidate.com",
                PasswordUtil.hashPassword("candidate123"),
                "Alice Johnson",
                Role.JOB_SEEKER,
                "+1 (555) 728-1193"
        );
        userRepository.save(cand1);

        UserProfile prof1 = new UserProfile(cand1);
        prof1.setHeadline("Senior Full-Stack Java & Cloud Developer");
        prof1.setLocation("Austin, TX");
        prof1.setSkills("Java 21, Spring Boot, Spring Data JPA, Microservices, React, PostgreSQL, Docker, AWS, Git");
        prof1.setBio("Passionate backend and full-stack software engineer with 5+ years of experience designing scalable RESTful web APIs and enterprise microservices.");
        prof1.setExperience("Senior Software Engineer at Apex Cloud Solutions (2022-Present): Led backend migration to Spring Boot microservices, reduced latency by 35%.\nSoftware Engineer at ByteCraft (2020-2022): Developed Java web services and SQL database schemas.");
        prof1.setEducation("B.S. in Computer Science - University of Texas at Austin (2016-2020)");
        prof1.setResumeUrl("https://example.com/resumes/alice-johnson-resume.pdf");
        prof1.setPortfolioUrl("https://alicejohnson.dev");
        userProfileRepository.save(prof1);

        // 5. Candidate 2 - Bob
        User cand2 = new User(
                "bob@candidate.com",
                PasswordUtil.hashPassword("candidate123"),
                "Bob Williams",
                Role.JOB_SEEKER,
                "+1 (555) 902-3341"
        );
        userRepository.save(cand2);

        UserProfile prof2 = new UserProfile(cand2);
        prof2.setHeadline("DevOps & Platform Systems Engineer");
        prof2.setLocation("Seattle, WA");
        prof2.setSkills("Kubernetes, Docker, Terraform, CI/CD pipelines, AWS, Linux, Java, PostgreSQL, Prometheus");
        prof2.setBio("DevOps engineer specialized in zero-downtime deployments, infrastructure-as-code, and resilient observability for high-throughput cloud applications.");
        prof2.setExperience("Platform Engineer at CloudPeak (2021-Present): Maintained multi-cluster Kubernetes on AWS with 99.99% uptime.\nSystems Administrator at DataWave (2018-2021): Linux sysadmin and automated CI/CD.");
        prof2.setEducation("B.S. in Information Technology - University of Washington (2015-2019)");
        prof2.setResumeUrl("https://example.com/resumes/bob-williams-devops.pdf");
        prof2.setPortfolioUrl("https://github.com/bobwilliams-devops");
        userProfileRepository.save(prof2);

        // 6. Create realistic Job Postings
        Job job1 = new Job();
        job1.setEmployer(emp1);
        job1.setTitle("Senior Java Backend Engineer");
        job1.setCategory("Software Engineering");
        job1.setJobType("Full-time");
        job1.setLocation("Remote (US / Global)");
        job1.setSalaryMin(125000.0);
        job1.setSalaryMax(165000.0);
        job1.setExperienceLevel("Senior");
        job1.setDescription("We are seeking an experienced Senior Java Backend Engineer to design, architect, and optimize our next-generation cloud services and high-throughput data processing engines.");
        job1.setRequirements("• 5+ years building backend systems in Java with Spring Boot & Hibernate.\n• Strong relational SQL database design and query optimization skills.\n• Experience with RESTful APIs, event streaming (Kafka/RabbitMQ), and Docker.\n• Familiarity with AWS or Azure cloud infrastructure.");
        job1.setBenefits("• 100% remote work flexibility\n• Comprehensive health, dental, and vision insurance\n• 401(k) retirement matching (5%)\n• $2,500 annual learning & equipment stipend");
        job1.setDeadline(LocalDate.now().plusDays(30));
        job1.setStatus("ACTIVE");
        jobRepository.save(job1);

        Job job2 = new Job();
        job2.setEmployer(emp1);
        job2.setTitle("Frontend React & UI Engineer");
        job2.setCategory("Software Engineering");
        job2.setJobType("Full-time");
        job2.setLocation("San Francisco, CA / Hybrid");
        job2.setSalaryMin(110000.0);
        job2.setSalaryMax(140000.0);
        job2.setExperienceLevel("Mid-level");
        job2.setDescription("Join our core product team to craft fluid, accessible, and ultra-responsive user interfaces for thousands of enterprise business clients.");
        job2.setRequirements("• 3+ years experience with modern JavaScript / TypeScript, React, and HTML5/CSS3.\n• Proficient with state management and consumption of RESTful/GraphQL APIs.\n• Eye for pixel-perfect design systems and responsive web standards.");
        job2.setBenefits("• Flexible hybrid working hours\n• Generous paid time off (25 days)\n• Stock options and annual performance bonus");
        job2.setDeadline(LocalDate.now().plusDays(25));
        job2.setStatus("ACTIVE");
        jobRepository.save(job2);

        Job job3 = new Job();
        job3.setEmployer(emp2);
        job3.setTitle("Site Reliability & Cloud Platform Engineer");
        job3.setCategory("DevOps & Cloud");
        job3.setJobType("Full-time");
        job3.setLocation("Remote (US)");
        job3.setSalaryMin(135000.0);
        job3.setSalaryMax(175000.0);
        job3.setExperienceLevel("Senior");
        job3.setDescription("Help innovate our distributed financial platform's reliability, latency, automated failovers, and multi-region Kubernetes infrastructure.");
        job3.setRequirements("• 4+ years in SRE or DevOps roles supporting production workloads.\n• Hands-on mastery of Kubernetes, Docker, Terraform, and AWS/GCP.\n• Proficient with monitoring tools (Prometheus, Grafana, Datadog).\n• Scripting fluency in Bash, Python, or Go.");
        job3.setBenefits("• Competitive base salary + equity compensation\n• Flexible PTO and parental leave\n• Home office setup allowance ($1,500)");
        job3.setDeadline(LocalDate.now().plusDays(40));
        job3.setStatus("ACTIVE");
        jobRepository.save(job3);

        Job job4 = new Job();
        job4.setEmployer(emp2);
        job4.setTitle("Data Scientist & Machine Learning Specialist");
        job4.setCategory("Data & AI");
        job4.setJobType("Full-time");
        job4.setLocation("New York, NY");
        job4.setSalaryMin(130000.0);
        job4.setSalaryMax(165000.0);
        job4.setExperienceLevel("Mid-level");
        job4.setDescription("Develop predictive analytics models, fraud detection algorithms, and automated intelligence pipelines powering modern fintech services.");
        job4.setRequirements("• M.S. or B.S. in Computer Science, Statistics, Mathematics or related discipline.\n• 3+ years applying Machine Learning algorithms with Python (scikit-learn, PyTorch/TensorFlow).\n• Strong SQL skills for feature extraction and data preparation.\n• Experience deploying ML models to production APIs.");
        job4.setBenefits("• Cutting-edge GPU compute access\n• Full healthcare package\n• Commuter benefits and catered lunches");
        job4.setDeadline(LocalDate.now().plusDays(20));
        job4.setStatus("ACTIVE");
        jobRepository.save(job4);

        Job job5 = new Job();
        job5.setEmployer(emp1);
        job5.setTitle("Lead UI/UX Product Designer");
        job5.setCategory("Product & Design");
        job5.setJobType("Contract");
        job5.setLocation("Remote");
        job5.setSalaryMin(90000.0);
        job5.setSalaryMax(120000.0);
        job5.setExperienceLevel("Lead");
        job5.setDescription("Lead the product discovery, wireframing, interactive prototyping, and user journey design for our next major SaaS revision.");
        job5.setRequirements("• 5+ years of product/UX design experience for SaaS applications.\n• Expert in Figma, design systems, and rapid prototyping.\n• Portfolio demonstrating clear problem-solving and user-centered design.");
        job5.setBenefits("• Flexible contract engagement\n• Direct collaboration with VP of Product\n• Possibility of full-time conversion");
        job5.setDeadline(LocalDate.now().plusDays(15));
        job5.setStatus("ACTIVE");
        jobRepository.save(job5);

        Job job6 = new Job();
        job6.setEmployer(emp2);
        job6.setTitle("Software Engineering Intern (Summer 2026)");
        job6.setCategory("Software Engineering");
        job6.setJobType("Internship");
        job6.setLocation("New York, NY / Hybrid");
        job6.setSalaryMin(45000.0);
        job6.setSalaryMax(60000.0);
        job6.setExperienceLevel("Entry-level");
        job6.setDescription("Exciting opportunity for university students and early-career software developers to gain real-world experience building production financial applications with Java and modern web tools.");
        job6.setRequirements("• Pursuing a degree in Computer Science, Software Engineering, or related field.\n• Foundational understanding of Java, Object-Oriented Programming, and SQL.\n• Curiosity and eager attitude to learn with senior engineering mentors.");
        job6.setBenefits("• 1-on-1 mentorship from senior software architects\n• Housing stipend support\n• Strong pathway to full-time new graduate offer");
        job6.setDeadline(LocalDate.now().plusDays(45));
        job6.setStatus("ACTIVE");
        jobRepository.save(job6);

        // 7. Seed Initial Applications
        JobApplication app1 = new JobApplication(
                job1,
                cand1,
                "Dear Hiring Team,\n\nI am enthusiastic about the Senior Java Backend Engineer role at TechCorp. Having spent the last 5 years developing scalable Java and Spring Boot microservices, I am confident I can make an immediate impact on your cloud platform architecture.\n\nBest regards,\nAlice Johnson",
                prof1.getResumeUrl()
        );
        app1.setStatus("SHORTLISTED");
        app1.setRecruiterNotes("Strong Java and microservices experience. Scheduled for technical interview.");
        app1.setAppliedAt(LocalDateTime.now().minusDays(3));
        applicationRepository.save(app1);

        JobApplication app2 = new JobApplication(
                job3,
                cand2,
                "Hi Innovate Labs Team,\n\nWith my background in automated cloud platforms and multi-region Kubernetes deployments, I would love to support your mission-critical financial uptime requirements.\n\nSincerely,\nBob Williams",
                prof2.getResumeUrl()
        );
        app2.setStatus("INTERVIEW");
        app2.setRecruiterNotes("Impressive DevOps and Kubernetes background. System design interview scheduled.");
        app2.setAppliedAt(LocalDateTime.now().minusDays(2));
        applicationRepository.save(app2);

        JobApplication app3 = new JobApplication(
                job2,
                cand1,
                "Hello,\n\nI have extensive experience working with React and connecting frontend single-page apps with Java Spring REST services. Looking forward to discussing this opportunity!\n\nAlice",
                prof1.getResumeUrl()
        );
        app3.setStatus("APPLIED");
        app3.setAppliedAt(LocalDateTime.now().minusHours(18));
        applicationRepository.save(app3);
    }
}
