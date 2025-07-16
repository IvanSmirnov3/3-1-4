package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;

public interface UserService {

    List<User> findAllUser();

    void deleteUser(Long id);

    User findById(Long id);

    User createUserFromDTO(UserDTO userDTO);

    User updateUserFromDTO(Long id, UserDTO userDTO);

    List<UserDTO> findAllUserDTO();
}
