package com.saas.dental_clinic.service.impl;

import com.saas.dental_clinic.dto.LoginRequest;
import com.saas.dental_clinic.dto.LoginResponse;
import com.saas.dental_clinic.dto.RegisterRequest;
import com.saas.dental_clinic.model.Cabinet;
import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.model.User;
import com.saas.dental_clinic.repository.CabinetRepository;
import com.saas.dental_clinic.repository.RoleRepository;
import com.saas.dental_clinic.repository.UserRepository;
import com.saas.dental_clinic.security.JwtTokenProvider;
import com.saas.dental_clinic.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CabinetRepository cabinetRepository;

    @Override
    @Transactional
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // Le reste du code de la méthode authenticateUser reste inchangé
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getName().toString())
            .collect(Collectors.toSet());

        return new LoginResponse(
            jwt,
            "Bearer",
            user.getId(),
            user.getEmail(),
            roles
        );
    }

    @Override
    @Transactional
    public void registerUser(RegisterRequest signupRequest) {
        // Vérification si l'email existe déjà
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Création d'un nouvel utilisateur
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(new HashSet<>()); // Initialisation de l'ensemble des rôles

        // Attribution des rôles
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            // Attribution du rôle PATIENT par défaut
            Role defaultRole = roleRepository.findByName(Role.RoleType.ROLE_PATIENT)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.getRoles().add(defaultRole);
        } else {
            // Pour chaque RoleType dans la requête
            for (Role.RoleType roleType : signupRequest.getRoles()) {
                Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Role " + roleType + " not found"));
                user.getRoles().add(role);
            }
        }

        // Gestion du cabinet si spécifié
        if (signupRequest.getCabinetId() != null) {
            Cabinet cabinet = cabinetRepository.findById(signupRequest.getCabinetId())
                .orElseThrow(() -> new RuntimeException("Cabinet not found"));
            user.setCabinet(cabinet);
        }

        // Sauvegarde de l'utilisateur
        userRepository.save(user);
    }
}