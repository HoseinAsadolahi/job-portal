package se.jobportal.repository;

import se.jobportal.entity.JobPostActivity;
import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {
    List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);
    List<JobSeekerSave> findByJob(JobPostActivity job);
    void deleteAllByJob(JobPostActivity job);
}
