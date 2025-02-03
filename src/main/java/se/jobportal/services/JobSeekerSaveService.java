package se.jobportal.services;

import se.jobportal.entity.JobPostActivity;
import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.JobSeekerSave;
import se.jobportal.repository.JobSeekerSaveRepository;
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

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }

    public void deleteAll(JobPostActivity job) {
        jobSeekerSaveRepository.deleteAllByJob(job);
    }
}
