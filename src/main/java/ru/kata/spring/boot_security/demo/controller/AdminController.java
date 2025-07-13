package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.mapper.DtoMapper;
import ru.kata.spring.boot_security.demo.model.RoleDTO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUser().stream()
                .map(DtoMapper::toUserDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(DtoMapper.toUserDTO(user));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.findAll().stream()
                .map(DtoMapper::toRoleDTO)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        List<Integer> roleIds = (List<Integer>) body.get("roles");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User savedUser = userService.createUser(user, roleIds.stream().map(Long::valueOf).toList());
        return ResponseEntity.ok(DtoMapper.toUserDTO(savedUser));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        List<Integer> roleIds = (List<Integer>) body.get("roles");

        User formUser = new User();
        formUser.setUsername(username);

        User updatedUser = userService.updateUser(id, formUser, roleIds.stream().map(Long::valueOf).toList(), password);
        return ResponseEntity.ok(DtoMapper.toUserDTO(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toUserDTO(userService.findById(id)));
    }
}
