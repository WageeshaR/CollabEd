package com.collabed.core.service;

import com.collabed.core.data.model.Role;
import com.collabed.core.data.model.User;
import com.collabed.core.data.repository.user.UserRepository;
import com.collabed.core.runtime.exception.OperationNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow();
    }

    public List<User> getAll() { return userRepository.findAll(); }

    public List<User> getAll(Role role) {
        return userRepository.findAllByRole(role).orElseGet(List::of);
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
