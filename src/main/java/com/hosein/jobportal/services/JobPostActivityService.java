package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.*;
import com.hosein.jobportal.repository.JobPostActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int id) {
        List<IRecruiterJobs> recruiterJobs = jobPostActivityRepository.getRecruiterJobs(id);
        List<RecruiterJobsDto> recruiterJobsDtoList = new ArrayList<>();
        for(IRecruiterJobs rj: recruiterJobs) {
            JobLocation location = new JobLocation(rj.getLocationId(), rj.getCity(), rj.getState(), rj.getCountry());
            JobCompany company = new JobCompany(rj.getCompanyId(), rj.getName(), "");
            recruiterJobsDtoList.add(new RecruiterJobsDto(rj.getTotalCandidates(), rj.getJob_post_id(),
                    rj.getJob_title(), location, company));
        }
        return recruiterJobsDtoList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote,
                                        LocalDate searchDate) {
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate( job, location,
                remote, type) : jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }

    public void deleteOne(int id) {
        jobPostActivityRepository.deleteById(id);
    }
}
