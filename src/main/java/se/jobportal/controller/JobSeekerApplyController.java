package se.jobportal.controller;

import se.jobportal.entity.*;
import se.jobportal.services.*;
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
import se.jobportal.entity.*;
import se.jobportal.services.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class JobSeekerApplyController {

    private final UserService us;
    private final JobPostActivityService js;
    private final JobSeekerApplyService jas;
    private final JobSeekerSaveService jss;
    private final RecruiterProfileService rps;
    private final JobSeekerProfileService jps;

    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobPostActivity jp = js.getOne(id);
        List<JobSeekerApply> applies = jas.getJobCandidates(jp);
        List<JobSeekerSave> saves = jss.getJobCandidates(jp);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile rp = rps.getCurrentRecruiterProfile();
                if (rp != null) {
                    model.addAttribute("applyList", applies);
                }
            } else {
                JobSeekerProfile jsp = jps.getCurrentUserProfile();
                if (jsp != null) {
                    boolean saved = false, exist = false;
                    for (JobSeekerApply jsa: applies) {
                        if (Objects.equals(jsa.getUserId().getUserAccountId(), jsp.getUserAccountId())) {
                            exist = true;
                            break;
                        }
                    }
                    for (JobSeekerSave jss: saves) {
                        if (Objects.equals(jss.getUserId().getUserAccountId(), jsp.getUserAccountId())) {
                            saved = true;
                            break;
                        }
                    }
                    model.addAttribute("alreadyApplied", exist);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }
        JobSeekerApply jsa = new JobSeekerApply();
        model.addAttribute("applyJob", jsa);
        model.addAttribute("jobDetails", jp);
        model.addAttribute("user", us.getCurrentUserProfile());
        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, Model model) {
        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = us.findByEmail(username);
            Optional<JobSeekerProfile> jobSeekerProfile = jps.getOne(user.getUserId());
            JobPostActivity jobPostActivity = js.getOne(id);
            if (jobSeekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply.setUserId(jobSeekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());
            } else {
                throw new RuntimeException("User not found");
            }
            jas.addNew(jobSeekerApply);
        }
        return "redirect:/dashboard/";
    }
}
