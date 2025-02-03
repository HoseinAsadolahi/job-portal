package se.jobportal.repository;

import se.jobportal.entity.JobPostActivity;
import se.jobportal.entity.JobSeekerApply;
import se.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

    void deleteAllByJob(JobPostActivity job);
}
