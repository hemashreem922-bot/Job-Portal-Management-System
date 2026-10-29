package com.jobportal.service;

import com.jobportal.dto.AuthRequest;
import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.model.Company;
import com.jobportal.model.Role;
import com.jobportal.model.User;
import com.jobportal.model.UserProfile;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.UserProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CompanyRepository companyRepository;

    public AuthService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        User user = new User(
                request.getEmail().toLowerCase().trim(),
                hashedPassword,
                request.getFullName().trim(),
                request.getRole(),
                request.getPhone()
        );
        user = userRepository.save(user);

        String companyName = null;
        if (user.getRole() == Role.JOB_SEEKER) {
            UserProfile profile = new UserProfile(user);
            userProfileRepository.save(profile);
        } else if (user.getRole() == Role.EMPLOYER) {
            companyName = (request.getCompanyName() != null && !request.getCompanyName().isBlank())
                    ? request.getCompanyName().trim()
                    : user.getFullName() + "'s Organization";
            Company company = new Company(user, companyName, "Technology", "", "", "");
            companyRepository.save(company);
        }

        String token = UUID.randomUUID().toString();
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                companyName,
                token,
                "Registration successful"
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isActive()) {
            throw new IllegalStateException("Your account has been deactivated. Please contact support.");
        }

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String companyName = null;
        if (user.getRole() == Role.EMPLOYER) {
            companyName = companyRepository.findByUserId(user.getId())
                    .map(Company::getCompanyName)
                    .orElse(user.getFullName() + "'s Organization");
        }

        String token = UUID.randomUUID().toString();
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                companyName,
                token,
                "Login successful"
        );
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
}
