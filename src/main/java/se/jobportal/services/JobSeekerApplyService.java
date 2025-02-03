package se.jobportal.services;

import se.jobportal.entity.JobPostActivity;
import se.jobportal.entity.JobSeekerApply;
import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.repository.JobSeekerApplyRepository;
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

    public void addNew(JobSeekerApply jobSeekerApply) {
        jar.save(jobSeekerApply);
    }

    public void deleteAll(JobPostActivity job) {
        jar.deleteAllByJob(job);
    }
}
