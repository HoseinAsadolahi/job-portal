package com.hosein.jobportal.service;

import com.hosein.jobportal.entity.*;
import com.hosein.jobportal.repository.JobPostActivityRepository;
import com.hosein.jobportal.services.JobPostActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





public class JobPostActivityServiceTest {

    @Mock
    private JobPostActivityRepository jobPostActivityRepository;

    @InjectMocks
    private JobPostActivityService jobPostActivityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNew() {
        JobPostActivity jobPostActivity = new JobPostActivity();
        when(jobPostActivityRepository.save(jobPostActivity)).thenReturn(jobPostActivity);

        JobPostActivity result = jobPostActivityService.addNew(jobPostActivity);

        assertEquals(jobPostActivity, result);
        verify(jobPostActivityRepository, times(1)).save(jobPostActivity);
    }

    @Test
    public void testGetRecruiterJobs() {
        int recruiterId = 1;
        IRecruiterJobs recruiterJob = mock(IRecruiterJobs.class);
        when(recruiterJob.getLocationId()).thenReturn(1);
        when(recruiterJob.getCity()).thenReturn("City");
        when(recruiterJob.getState()).thenReturn("State");
        when(recruiterJob.getCountry()).thenReturn("Country");
        when(recruiterJob.getCompanyId()).thenReturn(1);
        when(recruiterJob.getName()).thenReturn("Company");
        when(recruiterJob.getTotalCandidates()).thenReturn(10L);
        when(recruiterJob.getJob_post_id()).thenReturn(1);
        when(recruiterJob.getJob_title()).thenReturn("Job Title");

        when(jobPostActivityRepository.getRecruiterJobs(recruiterId)).thenReturn(Arrays.asList(recruiterJob));

        List<RecruiterJobsDto> result = jobPostActivityService.getRecruiterJobs(recruiterId);

        assertEquals(1, result.size());
        assertEquals("City", result.get(0).getJobLocationId().getCity());
        verify(jobPostActivityRepository, times(1)).getRecruiterJobs(recruiterId);
    }

    @Test
    public void testGetOne() {
        int jobId = 1;
        JobPostActivity jobPostActivity = new JobPostActivity();
        when(jobPostActivityRepository.findById(jobId)).thenReturn(Optional.of(jobPostActivity));

        JobPostActivity result = jobPostActivityService.getOne(jobId);

        assertEquals(jobPostActivity, result);
        verify(jobPostActivityRepository, times(1)).findById(jobId);
    }

    @Test
    public void testGetOneNotFound() {
        int jobId = 1;
        when(jobPostActivityRepository.findById(jobId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            jobPostActivityService.getOne(jobId);
        });

        assertEquals("Job not found", exception.getMessage());
        verify(jobPostActivityRepository, times(1)).findById(jobId);
    }

    @Test
    public void testGetAll() {
        JobPostActivity jobPostActivity1 = new JobPostActivity();
        JobPostActivity jobPostActivity2 = new JobPostActivity();
        when(jobPostActivityRepository.findAll()).thenReturn(Arrays.asList(jobPostActivity1, jobPostActivity2));

        List<JobPostActivity> result = jobPostActivityService.getAll();

        assertEquals(2, result.size());
        verify(jobPostActivityRepository, times(1)).findAll();
    }

    @Test
    public void testSearchWithDate() {
        String job = "Developer";
        String location = "City";
        List<String> type = Arrays.asList("Full-time");
        List<String> remote = Arrays.asList("Yes");
        LocalDate searchDate = LocalDate.now();

        JobPostActivity jobPostActivity = new JobPostActivity();
        when(jobPostActivityRepository.search(job, location, remote, type, searchDate)).thenReturn(Arrays.asList(jobPostActivity));

        List<JobPostActivity> result = jobPostActivityService.search(job, location, type, remote, searchDate);

        assertEquals(1, result.size());
        verify(jobPostActivityRepository, times(1)).search(job, location, remote, type, searchDate);
    }

    @Test
    public void testSearchWithoutDate() {
        String job = "Developer";
        String location = "City";
        List<String> type = Arrays.asList("Full-time");
        List<String> remote = Arrays.asList("Yes");

        JobPostActivity jobPostActivity = new JobPostActivity();
        when(jobPostActivityRepository.searchWithoutDate(job, location, remote, type)).thenReturn(Arrays.asList(jobPostActivity));

        List<JobPostActivity> result = jobPostActivityService.search(job, location, type, remote, null);

        assertEquals(1, result.size());
        verify(jobPostActivityRepository, times(1)).searchWithoutDate(job, location, remote, type);
    }

    @Test
    public void testDeleteOne() {
        int jobId = 1;
        doNothing().when(jobPostActivityRepository).deleteById(jobId);

        jobPostActivityService.deleteOne(jobId);

        verify(jobPostActivityRepository, times(1)).deleteById(jobId);
    }
}