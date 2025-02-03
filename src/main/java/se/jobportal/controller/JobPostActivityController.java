package se.jobportal.controller;

import se.jobportal.entity.*;
import se.jobportal.entity.*;
import se.jobportal.services.JobPostActivityService;
import se.jobportal.services.JobSeekerApplyService;
import se.jobportal.services.JobSeekerSaveService;
import se.jobportal.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class JobPostActivityController {

    private final UserService userService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @GetMapping({"/dashboard/", "/dashboard"})
    public String searchJobs(Model model, @RequestParam(value = "job", required = false) String job,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "partTime", required = false) String partTime,
                             @RequestParam(value = "fullTime", required = false) String fullTime,
                             @RequestParam(value = "freelance", required = false) String freelance,
                             @RequestParam(value = "internship", required = false) String internship,
                             @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly", required = false) String officeOnly,
                             @RequestParam(value = "partialRemote", required = false) String partialRemote,
                             @RequestParam(value = "today", required = false) boolean today,
                             @RequestParam(value = "days7", required = false) boolean days7,
                             @RequestParam(value = "days30", required = false) boolean days30) {
        model.addAttribute("partTime", Objects.equals(partTime, "Part-time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("internship", Objects.equals(internship, "Internship"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);
        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlags = true;
        boolean remote = true;
        boolean type = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlags = false;
        }
        if (partTime == null && fullTime == null && freelance == null && internship == null) {
            partTime = "Part-time";
            fullTime = "Full-time";
            freelance = "Freelance";
            internship = "Internship";
            remote = false;
        }
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            partialRemote = "Partial-Remote";
            remoteOnly = "Remote-Only";
            type = false;
        }
        if (!dateSearchFlags && !remote && !type && !StringUtils.hasText(job) &&
                !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance, internship),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }
        Object currentUserProfile = userService.getCurrentUserProfile();
        model.addAttribute("user", currentUserProfile);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                model.addAttribute("jobPost", jobPostActivityService.
                        getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId()));
            } else {
                List<JobSeekerApply> jobSeekerApplies = jobSeekerApplyService.
                        getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaves = jobSeekerSaveService.
                        getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                boolean exist, saved;
                for (JobPostActivity jobActivity : jobPost) {
                    exist = false;
                    saved = false;
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplies) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.
                                getJob().getJobPostId())) {
                            jobActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }
                    for (JobSeekerSave jobSeekerSave: jobSeekerSaves) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                            jobActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }
                    if (!exist) {
                        jobActivity.setIsActive(false);
                    }
                    if (!saved) {
                        jobActivity.setIsSaved(false);
                    }
                    model.addAttribute("jobPost", jobPost);
                }
            }
        }
        return "dashboard";
    }

    @GetMapping("/dashboard/add")
    public String addJob(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", userService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNewJob(JobPostActivity jobPostActivity, Model model) {
        User user = userService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        JobPostActivity saved = jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }

    @GetMapping("dashboard/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        JobPostActivity jp = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jp);
        model.addAttribute("user", userService.getCurrentUserProfile());
        return "add-jobs";
    }

    @Transactional
    @PostMapping("/dashboard/deleteJob/{id}")
    public String delete(@PathVariable("id") int id) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        jobSeekerApplyService.deleteAll(jobPostActivity);
        jobSeekerSaveService.deleteAll(jobPostActivity);
        jobPostActivityService.deleteOne(id);
        return "redirect:/dashboard/";
    }

    @GetMapping("global-search/")
    public String globalSearch(Model model, @RequestParam(value = "job", required = false) String job,
                               @RequestParam(value = "location", required = false) String location,
                               @RequestParam(value = "partTime", required = false) String partTime,
                               @RequestParam(value = "fullTime", required = false) String fullTime,
                               @RequestParam(value = "freelance", required = false) String freelance,
                               @RequestParam(value = "internship", required = false) String internship,
                               @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                               @RequestParam(value = "officeOnly", required = false) String officeOnly,
                               @RequestParam(value = "partialRemote", required = false) String partialRemote,
                               @RequestParam(value = "today", required = false) boolean today,
                               @RequestParam(value = "days7", required = false) boolean days7,
                               @RequestParam(value = "days30", required = false) boolean days30) {
        model.addAttribute("partTime", Objects.equals(partTime, "Part-time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("internship", Objects.equals(internship, "Internship"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);
        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlags = true;
        boolean remote = true;
        boolean type = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlags = false;
        }
        if (partTime == null && fullTime == null && freelance == null && internship == null) {
            partTime = "Part-time";
            fullTime = "Full-time";
            freelance = "Freelance";
            internship = "Internship";
            remote = false;
        }
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            partialRemote = "Partial-Remote";
            remoteOnly = "Remote-Only";
            type = false;
        }
        if (!dateSearchFlags && !remote && !type && !StringUtils.hasText(job) &&
                !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance, internship),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }
        model.addAttribute("jobPost", jobPost);
        return "global-search";
    }
}
