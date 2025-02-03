package com.hosein.jobportal.service;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.JobSeekerProfile;
import com.hosein.jobportal.entity.JobSeekerSave;
import com.hosein.jobportal.repository.JobSeekerSaveRepository;
import com.hosein.jobportal.services.JobSeekerSaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerSaveServiceTest {

    @Mock
    private JobSeekerSaveRepository jobSeekerSaveRepository;

    @InjectMocks
    private JobSeekerSaveService jobSeekerSaveService;

    private JobSeekerProfile testProfile;
    private JobPostActivity testJob;
    private JobSeekerSave testSave;

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

        testSave = JobSeekerSave.builder()
                .id(1)
                .userId(testProfile)
                .job(testJob)
                .build();
    }

    @Test
    @DisplayName("Should get candidate's saved jobs")
    void shouldGetCandidatesJobs() {
        // Arrange
        List<JobSeekerSave> expectedSaves = Arrays.asList(testSave);
        when(jobSeekerSaveRepository.findByUserId(testProfile)).thenReturn(expectedSaves);

        // Act
        List<JobSeekerSave> actualSaves = jobSeekerSaveService.getCandidatesJobs(testProfile);

        // Assert
        assertEquals(expectedSaves, actualSaves);
        verify(jobSeekerSaveRepository).findByUserId(testProfile);
    }

    @Test
    @DisplayName("Should get job's candidates")
    void shouldGetJobCandidates() {
        // Arrange
        List<JobSeekerSave> expectedSaves = Arrays.asList(testSave);
        when(jobSeekerSaveRepository.findByJob(testJob)).thenReturn(expectedSaves);

        // Act
        List<JobSeekerSave> actualSaves = jobSeekerSaveService.getJobCandidates(testJob);

        // Assert
        assertEquals(expectedSaves, actualSaves);
        verify(jobSeekerSaveRepository).findByJob(testJob);
    }

    @Test
    @DisplayName("Should add new save")
    void shouldAddNewSave() {
        // Act
        jobSeekerSaveService.addNew(testSave);

        // Assert
        verify(jobSeekerSaveRepository).save(testSave);
    }

    @Test
    @DisplayName("Should delete all saves for job")
    void shouldDeleteAllSavesForJob() {
        // Act
        jobSeekerSaveService.deleteAll(testJob);

        // Assert
        verify(jobSeekerSaveRepository).deleteAllByJob(testJob);
    }
} 