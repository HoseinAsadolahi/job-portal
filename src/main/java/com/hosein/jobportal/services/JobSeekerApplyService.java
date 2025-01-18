package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerApply;
import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.repository.JobSeekerApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSeekerApplyService {
    private final JobSeekerApplyRepository jar;

    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile jobSeekerProfile) {
        return jar.findByUserId(jobSeekerProfile);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
        return jar.findByJob(job);
    }
}
