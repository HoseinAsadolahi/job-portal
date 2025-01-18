package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.entity.JobSeekerSave;
import com.hosein.jobportal.repository.JobSeekerSaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public List<JobSeekerSave> getCandidatesJobs(JobSeekerProfile profile) {
        return jobSeekerSaveRepository.findByUserId(profile);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job) {
        return jobSeekerSaveRepository.findByJob(job);
    }
}
