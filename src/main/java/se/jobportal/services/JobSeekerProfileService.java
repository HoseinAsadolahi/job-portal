package se.jobportal.services;

import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.User;
import se.jobportal.repository.JobSeekerProfileRepository;
import se.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jpr;
    private final UserRepository userRepository;

    public Optional<JobSeekerProfile> getOne(int id) {
        return jpr.findById(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jpr.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new RuntimeException("User not found"));
            Optional<JobSeekerProfile> jobSeekerProfile = getOne(user.getUserId());
            return jobSeekerProfile .orElse(null);
        }
        return null;
    }
}
