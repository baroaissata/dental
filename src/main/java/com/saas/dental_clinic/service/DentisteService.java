package com.saas.dental_clinic.service;


import com.saas.dental_clinic.model.Dentiste;
import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.model.User;
import com.saas.dental_clinic.repository.DentisteRepository;
import com.saas.dental_clinic.repository.RoleRepository;
import com.saas.dental_clinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DentisteService {

    private final DentisteRepository dentisteRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public DentisteService(DentisteRepository dentisteRepository, UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.dentisteRepository = dentisteRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    // Méthode pour créer un dentiste

    public String createDentiste(Dentiste dentiste, String password, User loggedInUser) {
        // Vérifier que l'utilisateur est un chef de cabinet
        if (!loggedInUser.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF)) {
            return "Accès refusé. Seul le chef de cabinet peut créer un dentiste.";
        }

        try {
            // Créer le compte utilisateur pour le dentiste
            User userDentiste = new User();
            userDentiste.setFirstName(dentiste.getFirstName());
            userDentiste.setLastName(dentiste.getLastName());
            userDentiste.setEmail(dentiste.getEmail());
            userDentiste.setPassword(passwordEncoder.encode(password));
            userDentiste.setCabinet(loggedInUser.getCabinet());
            userDentiste.setEnabled(true);

            // Ajouter le rôle DENTISTE
            Optional<Role> roleDentiste = roleRepository.findByName(Role.RoleType.ROLE_DENTIST);
            Set<Role> roles = new HashSet<>();
            roleDentiste.ifPresent(roles::add);
            userDentiste.setRoles(roles);


            // Sauvegarder l'utilisateur
            userDentiste = userRepository.save(userDentiste);

            // Configurer et sauvegarder le dentiste
            dentiste.setUser(userDentiste);
            dentiste.setCabinet(loggedInUser.getCabinet());
            dentisteRepository.save(dentiste);

            return "Dentiste créé avec succès.";
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du dentiste: " + e.getMessage());
        }
    }

    public List<Dentiste> listDentistes(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est un chef de cabinet
        if (!user.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF)) {
            throw new RuntimeException("Accès refusé. Seul le chef de cabinet peut lister les dentistes.");
        }

        // Retourner tous les dentistes du cabinet du chef
        return dentisteRepository.findByCabinet(user.getCabinet());
    }


    public String updateDentiste(Long id, Dentiste updatedDentiste, UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new RuntimeException("Utilisateur non authentifié !");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        System.out.println("🔍 Tentative de mise à jour du dentiste ID: " + id);
        System.out.println("👤 Utilisateur authentifié : " + userDetails.getUsername());
        System.out.println("📧 Email utilisateur: " + user.getEmail());
        System.out.println("🏥 Cabinet utilisateur: " + user.getCabinet());


        // Vérifier si l'utilisateur est un chef de cabinet
        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals(Role.RoleType.ROLE_CABINET_CHIEF))) {
            throw new RuntimeException("Accès refusé. Seul le chef de cabinet peut modifier un dentiste.");
        }

        Dentiste existingDentiste = dentisteRepository.findById(id)
                .orElseThrow(() -> {
                    System.err.println("❌ Dentiste non trouvé pour ID: " + id);
                    return new RuntimeException("Dentiste non trouvé");
                });
        System.out.println("👨‍⚕️ Dentiste avant update : " + existingDentiste);


        // Vérifier que le dentiste appartient au même cabinet
        if (!existingDentiste.getCabinet().equals(user.getCabinet())) {
            throw new RuntimeException("Accès refusé. Le dentiste n'appartient pas à votre cabinet.");
        }

        // Mettre à jour les informations du dentiste
        existingDentiste.setFirstName(updatedDentiste.getFirstName());
        existingDentiste.setLastName(updatedDentiste.getLastName());
        existingDentiste.setEmail(updatedDentiste.getEmail());
        existingDentiste.setPhoneNumber(updatedDentiste.getPhoneNumber());

        dentisteRepository.save(existingDentiste);
        return "Dentiste mis à jour avec succès.";
    }

    public String deleteDentiste(Long id, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est un chef de cabinet
        if (!user.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF)) {
            throw new RuntimeException("Accès refusé. Seul le chef de cabinet peut supprimer un dentiste.");
        }

        Dentiste dentiste = dentisteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentiste non trouvé"));

        // Vérifier que le dentiste appartient au même cabinet
        if (!dentiste.getCabinet().equals(user.getCabinet())) {
            throw new RuntimeException("Accès refusé. Le dentiste n'appartient pas à votre cabinet.");
        }

        dentisteRepository.delete(dentiste);
        return "Dentiste supprimé avec succès.";
    }
}
