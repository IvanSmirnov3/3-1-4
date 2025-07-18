package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dto.UserDto;

import java.util.List;

public interface UserService {

    List<User> findAllUser();

    void deleteUser(Long id);

    User findById(Long id);

    User createUserFromDto(UserDto userDto);

    UserDto createUserReturnDto(UserDto userDto);

    User updateUserFromDto(Long id, UserDto userDto);

    List<UserDto> findAllUserDto();

    UserDto getUserDtoById(Long id);

    UserDto getCurrentUserDto(User user);

    UserDto updateUserReturnDto(Long id, UserDto userDto);
}
