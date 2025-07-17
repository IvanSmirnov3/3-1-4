package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.mapper.DtoMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dto.UserDto;
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
    public List<UserDto> findAllUserDto() {
        return findAllUser().stream()
                .map(DtoMapper::toUserDto)
                .toList();
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
    public UserDto getUserDtoById(Long id) {
        User user = findById(id);
        return DtoMapper.toUserDto(user);
    }

    @Override
    public UserDto getCurrentUserDto(User user) {
        return DtoMapper.toUserDto(user);
    }

    @Override
    public User createUserFromDto(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (userDto.getRoles() != null) {
            List<Long> roleIds = userDto.getRoles().stream()
                    .map(RoleDto::getId)
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
    public UserDto createUserReturnDto(UserDto userDto) {
        User user = createUserFromDto(userDto);
        return DtoMapper.toUserDto(user);
    }

    @Override
    public User updateUserFromDto(Long id, UserDto userDto) {
        User userFromDb = findById(id);

        userFromDb.setFirstName(userDto.getFirstName());
        userFromDb.setLastName(userDto.getLastName());
        userFromDb.setEmail(userDto.getEmail());
        userFromDb.setAge(userDto.getAge());

        if (userDto.getRoles() != null) {
            List<Long> roleIds = userDto.getRoles().stream()
                    .map(RoleDto::getId)
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

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userFromDb.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return userRepository.save(userFromDb);
    }

    @Override
    public UserDto updateUserReturnDto(Long id, UserDto userDto) {
        User updatedUser = updateUserFromDto(id, userDto);
        return DtoMapper.toUserDto(updatedUser);
    }
}
