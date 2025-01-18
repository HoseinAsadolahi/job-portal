package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.repository.JobSeekerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jpr;

    public Optional<JobSeekerProfile> getOne(int id) {
        return jpr.findById(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jpr.save(jobSeekerProfile);
    }
}
