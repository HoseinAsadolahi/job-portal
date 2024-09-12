package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.User;
import com.hosein.jobportal.exception.DuplicateEmailException;
import com.hosein.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        user.setActive(true);
        user.setRegistrationDate(new Date());
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists!");
        }
    }
}
