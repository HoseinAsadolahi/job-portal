package se.jobportal.controller;

import se.jobportal.entity.RecruiterProfile;
import se.jobportal.entity.User;
import se.jobportal.repository.UserRepository;
import se.jobportal.services.RecruiterProfileService;
import se.jobportal.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {
    private final UserRepository userRepository;
    private final RecruiterProfileService recruiterProfileService;

    @GetMapping("/")
    public String editRecruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("Username not found"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.findById(user.getUserId());
            recruiterProfile.ifPresent(profile -> model.addAttribute("profile", profile));
        }
        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile file, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("Username not found"));
            recruiterProfile.setUserAccountId(user.getUserId());
            recruiterProfile.setUser(user);
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            fileName = StringUtils.cleanPath(file.getOriginalFilename());
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile saved = recruiterProfileService.addNew(recruiterProfile);
        String uploadDir =  "photos/recruiter/" + saved.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }
}
