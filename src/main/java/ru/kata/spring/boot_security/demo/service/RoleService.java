package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.RoleDTO;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Role findByName(String name);

    List<Role> getRolesByIds(List<Long> ids);

    List<RoleDTO> findAllRoleDTO();
}
