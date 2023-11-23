package com.collabed.core.service;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.AdminRepository;
import com.collabed.core.data.repository.user.FacilitatorRepository;
import com.collabed.core.data.repository.user.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final StudentRepository studentRepository;
    private final FacilitatorRepository facilitatorRepository;
    private final AdminRepository adminRepository;

    public List<User> getAll(Role role) {
        List<User> users = null;
        switch (role) {
            case ADMIN -> users = adminRepository.findAll();
            case STUDENT -> users = studentRepository.findAll();
            case FACILITATOR -> users = facilitatorRepository.findAll();
            case SUPER_ADMIN -> {
                Optional<User> superAdmin = adminRepository.findAllByRole(role);
                users = superAdmin.map(List::of).orElseGet(List::of);
            }
        }
        return users;
    }

    public User register(User user) {
        Role role = Role.ADMIN;
        switch (role) {
            case ADMIN -> {return adminRepository.insert(user);}
            case STUDENT -> {return studentRepository.insert(user);}
            case FACILITATOR -> {return facilitatorRepository.insert(user);}
            case SUPER_ADMIN -> throw new IllegalArgumentException();
        }
        throw new InvalidParameterException();
    }
}
