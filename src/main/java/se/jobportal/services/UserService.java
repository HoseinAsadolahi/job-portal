package se.jobportal.services;

import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.RecruiterProfile;
import se.jobportal.entity.User;
import se.jobportal.exception.DuplicateEmailException;
import se.jobportal.repository.JobSeekerProfileRepository;
import se.jobportal.repository.RecruiterProfileRepository;
import se.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jspRepository;
    private final RecruiterProfileRepository rpRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        user.setActive(true);
        user.setRegistrationDate(new Date());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public Object getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Job Seeker"))) {
                return jspRepository.findById(user.getUserId()).orElse(new JobSeekerProfile(user));
            } else {
                return rpRepository.findById(user.getUserId()).orElse(new RecruiterProfile(user));
            }
        }
        return null;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
        }
        return null;
    }

    public User findByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
