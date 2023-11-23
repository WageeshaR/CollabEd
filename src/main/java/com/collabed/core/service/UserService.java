package com.collabed.core.service;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.OperationNotAllowedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll(Role role) {
        return userRepository.findAllByRole(Role.STUDENT).orElseGet(List::of);
    }

    public User register(User user) {
        Role role = user.getRole();
        return switch (role) {
            case SUPER_ADMIN ->
                    throw new OperationNotAllowedException("Not allowed to create more than one SUPER_ADMIN roles");
            case ADMIN, FACILITATOR, STUDENT -> userRepository.insert(user);
        };
    }
}
