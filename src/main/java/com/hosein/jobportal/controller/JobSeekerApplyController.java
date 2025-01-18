package com.hosein.jobportal.controller;

import com.hosein.jobportal.entity.JobPostActivity;
import com.hosein.jobportal.services.JobPostActivityService;
import com.hosein.jobportal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class JobSeekerApplyController {

    private final UserService us;
    private final JobPostActivityService js;

    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobPostActivity jp = js.getOne(id);
        model.addAttribute("jobDetails", jp);
        model.addAttribute("user", us.getCurrentUserProfile());
        return "job-details";
    }
}
