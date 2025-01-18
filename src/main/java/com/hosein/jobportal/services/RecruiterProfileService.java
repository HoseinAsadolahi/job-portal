package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.RecruiterProfile;
import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.repository.RecruiterProfileRepository;
import com.hosein.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileService {
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    public Optional<RecruiterProfile> findById(Integer id) {
        return recruiterProfileRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new RuntimeException("User not found"));
            Optional<RecruiterProfile> recruiterProfile = findById(user.getUserId());
            return recruiterProfile.orElse(null);
        }
        return null;
    }
}
