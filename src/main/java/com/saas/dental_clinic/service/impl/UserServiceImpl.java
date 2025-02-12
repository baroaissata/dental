package com.saas.dental_clinic.service.impl;

import com.saas.dental_clinic.dto.UserDto;
import com.saas.dental_clinic.model.Cabinet;
import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.model.User;
import com.saas.dental_clinic.repository.CabinetRepository;
import com.saas.dental_clinic.repository.RoleRepository;
import com.saas.dental_clinic.repository.UserRepository;
import com.saas.dental_clinic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CabinetRepository cabinetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        userDto.getRoles().forEach(roleName -> {
            Role role = roleRepository.findByName(Role.RoleType.valueOf(roleName))
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.getRoles().add(role);
        });

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            user.getRoles().clear();
            userDto.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(Role.RoleType.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found"));
                user.getRoles().add(role);
            });
        }

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF)) {
            Cabinet cabinet = cabinetRepository.findByChiefId(id).orElse(null);
            if (cabinet != null) {
                cabinet.setCabinetChief(null);
                cabinetRepository.save(cabinet);
            }
        }

        user.getRoles().clear();

        if (user.getCabinet() != null) {
            user.setCabinet(null);
        }

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByCabinetId(Long cabinetId) {
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new RuntimeException("Cabinet not found with id: " + cabinetId);
        }

        List<User> cabinetUsers = userRepository.findAllByCabinetId(cabinetId);

        return cabinetUsers.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getRoles().stream()
            .map(role -> role.getName().toString())
            .collect(Collectors.toSet()));
        return dto;
    }
}
