package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.repository.JobPostActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }
}
