package com.hosein.jobportal.services;

import com.hosein.jobportal.entity.UserType;
import com.hosein.jobportal.repository.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTypeService {

    private final UserTypeRepository userTypeRepository;

    public List<UserType> findAll() {
        return userTypeRepository.findAll();
    }
}
