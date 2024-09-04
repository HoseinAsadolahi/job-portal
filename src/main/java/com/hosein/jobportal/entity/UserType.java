package com.hosein.jobportal.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userTypeId;

    private String userTypeName;

    @OneToMany(targetEntity = User.class, cascade = CascadeType.ALL, mappedBy = "userType")
    private List<User> users;
}
