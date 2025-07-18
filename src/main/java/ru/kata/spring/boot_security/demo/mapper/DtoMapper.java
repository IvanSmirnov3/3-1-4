package ru.kata.spring.boot_security.demo.mapper;

import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.*;

import java.util.Set;
import java.util.stream.Collectors;

public class DtoMapper {

    public static UserDto toUserDto(User user) {
        Set<RoleDto> roles = user.getRoles().stream()
                .map(DtoMapper::toRoleDto)
                .collect(Collectors.toSet());

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAge(),
                roles
        );
    }

    public static RoleDto toRoleDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }
}
