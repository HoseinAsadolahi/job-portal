package com.hosein.jobportal.repository;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerApply;
import com.hosein.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

    void deleteAllByJob(JobPostActivity job);
}
