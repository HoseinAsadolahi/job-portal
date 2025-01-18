package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.entity.RecruiterProfile;
import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.services.JobPostActivityService;
import com.hosein.jobportal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
@RequiredArgsConstructor
public class JobPostActivityController {

    private final UserService userService;
    private final JobPostActivityService jobPostActivityService;

    @GetMapping({"/dashboard/", "/dashboard"})
    public String searchJobs(Model model) {
        Object currentUserProfile = userService.getCurrentUserProfile();
        model.addAttribute("user", currentUserProfile);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                model.addAttribute("jobPost", jobPostActivityService.
                        getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId()));
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
}
