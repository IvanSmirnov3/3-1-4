package ru.kata.spring.boot_security.demo.mapper;

import ru.kata.spring.boot_security.demo.model.*;

import java.util.Set;
import java.util.stream.Collectors;

public class DtoMapper {

    public static UserDTO toUserDTO(User user) {
        Set<RoleDTO> roles = user.getRoles().stream()
                .map(DtoMapper::toRoleDTO)
                .collect(Collectors.toSet());

        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAge(),
                roles
        );
    }

    public static RoleDTO toRoleDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }
}
