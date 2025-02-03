package com.hosein.jobportal.service;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerApply;
import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.repository.JobSeekerApplyRepository;
import com.hosein.jobportal.services.JobSeekerApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSeekerApplyServiceTest {

    @Mock
    private JobSeekerApplyRepository jobSeekerApplyRepository;

    @InjectMocks
    private JobSeekerApplyService jobSeekerApplyService;

    private JobSeekerProfile testProfile;
    private JobPostActivity testJob;
    private JobSeekerApply testApply;

    @BeforeEach
    void setUp() {
        testProfile = JobSeekerProfile.builder()
                .userAccountId(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        testJob = JobPostActivity.builder()
                .jobTitle("Software Engineer")
                .build();

        testApply = JobSeekerApply.builder()
                .id(1)
                .userId(testProfile)
                .job(testJob)
                .applyDate(new Date())
                .coverLetter("Test application")
                .build();
    }

    @Test
    @DisplayName("Should get candidate's applied jobs")
    void shouldGetCandidatesJobs() {
        // Arrange
        List<JobSeekerApply> expectedApplications = Arrays.asList(testApply);
        when(jobSeekerApplyRepository.findByUserId(testProfile)).thenReturn(expectedApplications);

        // Act
        List<JobSeekerApply> actualApplications = jobSeekerApplyService.getCandidatesJobs(testProfile);

        // Assert
        assertEquals(expectedApplications, actualApplications);
        verify(jobSeekerApplyRepository).findByUserId(testProfile);
    }

    @Test
    @DisplayName("Should get job's applications")
    void shouldGetJobCandidates() {
        // Arrange
        List<JobSeekerApply> expectedApplications = Arrays.asList(testApply);
        when(jobSeekerApplyRepository.findByJob(testJob)).thenReturn(expectedApplications);

        // Act
        List<JobSeekerApply> actualApplications = jobSeekerApplyService.getJobCandidates(testJob);

        // Assert
        assertEquals(expectedApplications, actualApplications);
        verify(jobSeekerApplyRepository).findByJob(testJob);
    }

    @Test
    @DisplayName("Should add new application")
    void shouldAddNewApplication() {
        // Act
        jobSeekerApplyService.addNew(testApply);

        // Assert
        verify(jobSeekerApplyRepository).save(testApply);
    }

    @Test
    @DisplayName("Should delete all applications for job")
    void shouldDeleteAllApplicationsForJob() {
        // Act
        jobSeekerApplyService.deleteAll(testJob);

        // Assert
        verify(jobSeekerApplyRepository).deleteAllByJob(testJob);
    }
} 