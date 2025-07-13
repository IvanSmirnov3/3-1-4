package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User createUser(User user, List<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            roles.addAll(roleService.getRolesByIds(roleIds));
        }
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(Long id, User formUser, List<Long> roleIds, String rawPassword) {
        User userFromDb = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userFromDb.setUsername(formUser.getUsername());

        if (roleIds != null) {
            Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
            userFromDb.setRoles(roles);
        }

        if (rawPassword != null && !rawPassword.isEmpty()) {
            userFromDb.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(userFromDb);
    }

}
