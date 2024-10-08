package com.hosein.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password can't be blank!")
    @Length(min = 6, max = 20, message = "Password length must be between 6 and 20!")
    private String password;

    private boolean isActive;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date registrationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userTypeId", referencedColumnName = "userTypeId")
    private UserType userType;
}
