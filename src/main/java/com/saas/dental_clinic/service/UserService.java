package com.saas.dental_clinic.service;

import com.saas.dental_clinic.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    List<UserDto> getUsersByCabinetId(Long cabinetId);
}