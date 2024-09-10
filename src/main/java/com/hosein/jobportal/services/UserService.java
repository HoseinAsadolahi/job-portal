package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        user.setActive(true);
        user.setRegistrationDate(new Date());
        return userRepository.save(user);
    }
}
