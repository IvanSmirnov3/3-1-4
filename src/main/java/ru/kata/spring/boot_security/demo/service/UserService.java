package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> findAllUser();
    void deleteUser(Long id);
    User findById(Long id);
    User createUser(User user, List<Long> roleIds);
    User updateUser(Long id, User formUser, List<Long> roleIds, String rawPassword);
}
