package se.jobportal.controller;

import se.jobportal.entity.JobSeekerProfile;
import se.jobportal.entity.Skill;
import se.jobportal.entity.User;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.JobSeekerProfileService;
import se.jobportal.util.FileDownloadUtil;
import se.jobportal.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
@RequiredArgsConstructor
public class JobSeekerProfileController {

    private final JobSeekerProfileService js;
    private final UserRepository ur;

    @GetMapping("/")
    public String jobSeekerProfile(Model model) {
        JobSeekerProfile j = new JobSeekerProfile();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Skill> skills = new ArrayList<>();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            User user = ur.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<JobSeekerProfile> jp = js.getOne(user.getUserId());
            if (jp.isPresent()) {
                j = jp.get();
                if (j.getSkills().isEmpty()) {
                    skills.add(new Skill());
                    j.setSkills(skills);
                }
            }
            model.addAttribute("skills", skills);
            model.addAttribute("profile", j);
        }
        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jsp, @Param("image")MultipartFile image,
                         @Param("pdf")MultipartFile pdf, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            User user = ur.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            jsp.setUser(user);
            jsp.setUserAccountId(user.getUserId());
        }

        List<Skill> skills = new ArrayList<>();
        model.addAttribute("profile", jsp);
        model.addAttribute("skills", skills);
        for (Skill skill: jsp.getSkills()) {
            skill.setJobSeekerProfile(jsp);
        }
        String imageName = "";
        String resumeName = "";
        if (!Objects.equals(image.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jsp.setProfilePhoto(imageName);
        }
        if (!Objects.equals(pdf.getOriginalFilename(), "")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jsp.setResume(resumeName);
        }
        try {
            String uploadDir = "photos/candidate/" + jsp.getUserAccountId();
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            if (!Objects.equals(pdf.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JobSeekerProfile saved = js.addNew(jsp);
        return "redirect:/dashboard/";
    }

    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model) {
        JobSeekerProfile profile = js.getOne(id).orElseThrow(() ->
                new RuntimeException("Job Seeker Profile not found"));
        model.addAttribute("profile", profile);
        return "job-seeker-profile";
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName,
                                            @RequestParam(value = "userID") String userId) {
        try {
            Resource resource = FileDownloadUtil.getFileAsResource("photos/candidate/" +
                    userId, fileName);
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            String header = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, header)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error occurred: " + e.getMessage());
        }
    }
}
