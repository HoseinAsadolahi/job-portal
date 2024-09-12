package com.hosein.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruiter_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RecruiterProfile {

    @Id
    private int userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private User user;

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String country;

    private String company;

    @Column(nullable = true, length = 64)
    private String profilePhoto;
}
