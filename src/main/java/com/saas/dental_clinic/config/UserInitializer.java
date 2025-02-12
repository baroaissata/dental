package com.saas.dental_clinic.config;


import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.repository.RoleRepository;
import com.saas.dental_clinic.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.saas.dental_clinic.model.User;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.Set;

@Configuration
public class UserInitializer {
    @Bean
    @Transactional
    public CommandLineRunner initializeUsers(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            String emailChef = "chef@cabinet.com";

            // Vérifie si l'utilisateur existe déjà
            Optional<User> existingUser = userRepository.findByEmail(emailChef);
            if (existingUser.isEmpty()) {
                // Récupère le rôle de chef de cabinet
                Role roleChefCabinet = roleRepository.findByName(Role.RoleType.ROLE_CABINET_CHIEF)
                        .orElseThrow(() -> new RuntimeException("Le rôle ROLE_CABINET_CHIEF n'existe pas"));

                // Crée un nouvel utilisateur
                User chefCabinet = new User();
                chefCabinet.setFirstName("Aissata");
                chefCabinet.setLastName("BARO");
                chefCabinet.setEmail(emailChef);
                chefCabinet.setPassword(new BCryptPasswordEncoder().encode("password"));
                chefCabinet.setRoles(Set.of(roleChefCabinet));

                // Sauvegarde dans la base
                userRepository.save(chefCabinet);
                System.out.println("✅ Chef de cabinet créé avec succès !");
            } else {
                System.out.println("ℹ️ Chef de cabinet existe déjà.");
            }
        };
    }
}
