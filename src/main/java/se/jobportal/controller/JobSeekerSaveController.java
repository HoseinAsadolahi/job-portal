package se.jobportal.controller;

import se.jobportal.entity.JobPostActivity;
import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.JobSeekerSave;
import se.jobportal.entity.User;
import se.jobportal.services.JobPostActivityService;
import se.jobportal.services.JobSeekerProfileService;
import se.jobportal.services.JobSeekerSaveService;
import se.jobportal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class JobSeekerSaveController {
    private final UserService us;
    private final JobSeekerProfileService jps;
    private final JobPostActivityService js;
    private final JobSeekerSaveService jss;

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = us.findByEmail(username);
            Optional<JobSeekerProfile> jsp = jps.getOne(user.getUserId());
            JobPostActivity job = js.getOne(id);
            if (jsp.isPresent() && job != null) {
                jobSeekerSave.setJob(job);
                jobSeekerSave.setUserId(jsp.get());
            } else {
                throw new RuntimeException("User not found");
            }
            jss.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {
        List<JobPostActivity> jobPostActivities = new ArrayList<>();
        Object currentUserProfile = us.getCurrentUserProfile();
        List<JobSeekerSave> saves = jss.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave save: saves) {
            jobPostActivities.add(save.getJob());
        }
        model.addAttribute("jobPost", jobPostActivities);
        model.addAttribute("user", currentUserProfile);
        return "saved-jobs";
    }
}
