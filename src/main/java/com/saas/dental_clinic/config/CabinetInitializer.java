package com.saas.dental_clinic.config;

import com.saas.dental_clinic.model.Cabinet;
import com.saas.dental_clinic.model.User;
import com.saas.dental_clinic.repository.CabinetRepository;
import com.saas.dental_clinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;


import java.util.Optional;

public class CabinetInitializer {
    @Bean
    @Transactional
    public CommandLineRunner initializeCabinet(UserRepository userRepository, CabinetRepository cabinetRepository) {
        return args -> {
            String emailChef = "chef@cabinet.com";

            // Récupérer le chef de cabinet
            Optional<User> chefCabinetOpt = userRepository.findByEmail(emailChef);
            if (chefCabinetOpt.isEmpty()) {
                System.out.println("❌ Impossible de créer le cabinet : chef de cabinet non trouvé.");
                return;
            }
            User chefCabinet = chefCabinetOpt.get();

            // Vérifie si le cabinet existe déjà
            if (cabinetRepository.findByName("Cabinet Dentaire").isEmpty()) {
                Cabinet cabinet = new Cabinet();
                cabinet.setName("Cabinet Dentaire");
                cabinet.setAddress("123 rue du Cabinet");
                cabinet.setPhoneNumber("339002020");
                cabinet.setCabinetChief(chefCabinet);

                cabinetRepository.save(cabinet);
                System.out.println("✅ Cabinet créé avec succès !");
            } else {
                System.out.println("ℹ️ Cabinet existe déjà.");
            }
        };
    }

}
