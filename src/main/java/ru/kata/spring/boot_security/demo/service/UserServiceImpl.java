package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.mapper.DtoMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.RoleDTO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User createUserFromDTO(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (userDTO.getRoles() != null) {
            List<Long> roleIds = userDTO.getRoles().stream()
                    .map(RoleDTO::getId)
                    .toList();
            roles.addAll(roleService.getRolesByIds(roleIds));

            boolean hasAdmin = roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            boolean hasUser = roles.stream().anyMatch(r -> "ROLE_USER".equals(r.getName()));

            if (hasAdmin && !hasUser) {
                Role userRole = roleService.findByName("ROLE_USER");
                roles.add(userRole);
            }
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public User updateUserFromDTO(Long id, UserDTO userDTO) {
        User userFromDb = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userFromDb.setFirstName(userDTO.getFirstName());
        userFromDb.setLastName(userDTO.getLastName());
        userFromDb.setEmail(userDTO.getEmail());
        userFromDb.setAge(userDTO.getAge());

        if (userDTO.getRoles() != null) {
            List<Long> roleIds = userDTO.getRoles().stream()
                    .map(RoleDTO::getId)
                    .toList();
            Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
            boolean hasAdmin = roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            boolean hasUser = roles.stream().anyMatch(r -> "ROLE_USER".equals(r.getName()));

            if (hasAdmin && !hasUser) {
                Role userRole = roleService.findByName("ROLE_USER");
                roles.add(userRole);
            }

            userFromDb.setRoles(roles);
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userFromDb.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(userFromDb);
    }

    @Override
    public List<UserDTO> findAllUserDTO() {
        return findAllUser().stream()
                .map(DtoMapper::toUserDTO)
                .toList();
    }

}
