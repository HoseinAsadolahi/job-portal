package se.jobportal.services;

import se.jobportal.entity.UserType;
import se.jobportal.repository.UserTypeRepository;
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
