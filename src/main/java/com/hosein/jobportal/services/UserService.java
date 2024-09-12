package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.entity.RecruiterProfile;
import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.exception.DuplicateEmailException;
import com.hosein.jobportal.repository.JobSeekerProfileRepository;
import com.hosein.jobportal.repository.RecruiterProfileRepository;
import com.hosein.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jspRepository;
    private final RecruiterProfileRepository rpRepository;

    public User save(User user) {
        user.setActive(true);
        user.setRegistrationDate(new Date());
        User savedUser = null;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists!");
        }
        if (savedUser.getUserType().getUserTypeId() == 1) {
            rpRepository.save(new RecruiterProfile(savedUser));
        } else {
            jspRepository.save(new JobSeekerProfile(savedUser));
        }
        return savedUser;
    }
}
