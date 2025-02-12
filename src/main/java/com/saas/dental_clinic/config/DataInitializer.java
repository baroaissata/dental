package com.saas.dental_clinic.config;

import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.model.Role.RoleType;
import com.saas.dental_clinic.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initializeData(RoleRepository roleRepository) {
        return args -> {
            // Vérifie si des rôles existent déjà
            if (roleRepository.count() == 0) {
                // Crée un rôle pour chaque valeur de l'énumération RoleType
                List<Role> roles = Arrays.stream(RoleType.values())
                    .map(roleType -> {
                        Role role = new Role();
                        role.setName(roleType);
                        return role;
                    })
                    .collect(Collectors.toList());

                // Sauvegarde tous les rôles dans la base de données
                roleRepository.saveAll(roles);

                System.out.println("Les rôles suivants ont été initialisés :");
                roles.forEach(role -> System.out.println("- " + role.getName()));
            } else {
                System.out.println("Les rôles sont déjà initialisés dans la base de données.");
            }
        };
    }
}