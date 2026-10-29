package com.jobportal.service;

import com.jobportal.dto.CompanyDto;
import com.jobportal.dto.UserProfileDto;
import com.jobportal.model.Company;
import com.jobportal.model.User;
import com.jobportal.model.UserProfile;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.UserProfileRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CompanyRepository companyRepository;

    public ProfileService(UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getCandidateProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> userProfileRepository.save(new UserProfile(user)));

        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setHeadline(profile.getHeadline());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());
        dto.setSkills(profile.getSkills());
        dto.setExperience(profile.getExperience());
        dto.setEducation(profile.getEducation());
        dto.setResumeUrl(profile.getResumeUrl());
        dto.setPortfolioUrl(profile.getPortfolioUrl());
        return dto;
    }

    @Transactional
    public UserProfileDto updateCandidateProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone().trim());
        }
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfile(user));

        profile.setHeadline(dto.getHeadline());
        profile.setBio(dto.getBio());
        profile.setLocation(dto.getLocation());
        profile.setSkills(dto.getSkills());
        profile.setExperience(dto.getExperience());
        profile.setEducation(dto.getEducation());
        profile.setResumeUrl(dto.getResumeUrl());
        profile.setPortfolioUrl(dto.getPortfolioUrl());

        userProfileRepository.save(profile);
        return getCandidateProfile(userId);
    }

    @Transactional(readOnly = true)
    public CompanyDto getCompanyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Company company = companyRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Company newComp = new Company(user, user.getFullName() + "'s Organization", "", "", "", "");
                    return companyRepository.save(newComp);
                });

        CompanyDto dto = new CompanyDto();
        dto.setUserId(user.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIndustry(company.getIndustry());
        dto.setWebsite(company.getWebsite());
        dto.setLocation(company.getLocation());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setDescription(company.getDescription());
        return dto;
    }

    @Transactional
    public CompanyDto updateCompanyProfile(Long userId, CompanyDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Company company = companyRepository.findByUserId(userId)
                .orElseGet(() -> new Company(user, dto.getCompanyName(), "", "", "", ""));

        if (dto.getCompanyName() != null && !dto.getCompanyName().isBlank()) {
            company.setCompanyName(dto.getCompanyName().trim());
        }
        company.setIndustry(dto.getIndustry());
        company.setWebsite(dto.getWebsite());
        company.setLocation(dto.getLocation());
        company.setLogoUrl(dto.getLogoUrl());
        company.setDescription(dto.getDescription());

        companyRepository.save(company);
        return getCompanyProfile(userId);
    }
}
