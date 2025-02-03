package se.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "job_seeker_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class JobSeekerProfile {

    @Id
    private Integer userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private User user;

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String country;

    private String workAuthorization;

    private String employmentType;

    private String resume;

    @Column(length = 64)
    private String profilePhoto;

    @OneToMany(targetEntity = Skill.class, cascade = CascadeType.ALL, mappedBy = "jobSeekerProfile")
    private List<Skill> skills;

    public JobSeekerProfile(User user) {
        this.user = user;
    }

    public String getPhotosImagePath() {
        if (profilePhoto == null || userAccountId == null) {
            return null;
        }
        return "/photos/candidate/" + userAccountId + "/" + profilePhoto;
    }
}
