package com.saas.dental_clinic.service;


import com.saas.dental_clinic.model.CreneauHoraire;
import com.saas.dental_clinic.model.Dentiste;
import com.saas.dental_clinic.model.Role;
import com.saas.dental_clinic.model.User;
import com.saas.dental_clinic.repository.CreneauHoraireRepository;
import com.saas.dental_clinic.repository.DentisteRepository;
import com.saas.dental_clinic.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreneauHoraireService {
    private final CreneauHoraireRepository creneauHoraireRepository;
    private final UserRepository userRepository;
    private final DentisteRepository dentisteRepository;

    public CreneauHoraireService(CreneauHoraireRepository creneauHoraireRepository,
                           UserRepository userRepository,
                           DentisteRepository dentisteRepository) {
        this.creneauHoraireRepository = creneauHoraireRepository;
        this.userRepository = userRepository;
        this.dentisteRepository = dentisteRepository;
    }

    public CreneauHoraire createCreneauHoraire(CreneauHoraire creneauHoraire, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isCabinetChief = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF);
        boolean isDentist = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_DENTIST);

        if (!isCabinetChief && !isDentist) {
            throw new RuntimeException("Accès refusé. Seuls les dentistes et chefs de cabinet peuvent créer des créneaux.");
        }

        // Si c'est un dentiste qui crée son propre créneau
        if (isDentist) {
            // Vérifier que le dentiste crée un créneau pour lui-même
            Dentiste dentiste = dentisteRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Dentiste non trouvé"));
            creneauHoraire.setDentiste(dentiste);
        }
        // Si c'est un chef de cabinet qui crée le créneau
        else if (isCabinetChief) {
            // Récupérer le dentiste complet depuis la base de données
            Dentiste dentiste = dentisteRepository.findById(creneauHoraire.getDentiste().getId())
                    .orElseThrow(() -> new RuntimeException("Dentiste non trouvé"));

            // Vérifier que le dentiste appartient au même cabinet que le chef
            if (!dentiste.getCabinet().getId().equals(user.getCabinet().getId())) {
                throw new RuntimeException("Le dentiste n'appartient pas à votre cabinet");
            }

            creneauHoraire.setDentiste(dentiste);
        }


        // Vérifier le chevauchement des créneaux
        List<CreneauHoraire> existingCreneauHoraire = creneauHoraireRepository.findByDentisteIdAndDate(
                creneauHoraire.getDentiste().getId(), creneauHoraire.getDate());

        for (CreneauHoraire existing : existingCreneauHoraire) {
            if (isOverlapping(existing, creneauHoraire)) {
                throw new RuntimeException("Ce créneau chevauche un créneau existant");
            }
        }

        return creneauHoraireRepository.save(creneauHoraire);
    }

    public CreneauHoraire updateCreneauHoraire(Long id, CreneauHoraire updatedCreneauHoraire, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CreneauHoraire existingCreneauHoraire = creneauHoraireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau non trouvé"));

        boolean isCabinetChief = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF);
        boolean isDentist = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_DENTIST);

        if (!isCabinetChief && !isDentist) {
            throw new RuntimeException("Accès refusé");
        }

        // Mettre à jour les champs
        existingCreneauHoraire.setDate(updatedCreneauHoraire.getDate());
        existingCreneauHoraire.setHeureDebut(updatedCreneauHoraire.getHeureDebut());
        existingCreneauHoraire.setHeureFin(updatedCreneauHoraire.getHeureFin());
        existingCreneauHoraire.setStatut(updatedCreneauHoraire.getStatut());

        return creneauHoraireRepository.save(existingCreneauHoraire);
    }
    public void deleteCreneauHoraire(Long id, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CreneauHoraire creneauHoraire = creneauHoraireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau non trouvé"));

        boolean isCabinetChief = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF);
        boolean isDentist = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_DENTIST);

        if (!isCabinetChief && !isDentist) {
            throw new RuntimeException("Accès refusé");
        }

        creneauHoraireRepository.delete(creneauHoraire);
    }

    public List<CreneauHoraire> getCreneauHoraireByDentist(Long dentisteId) {
        return creneauHoraireRepository.findByDentisteId(dentisteId);
    }

    private boolean isOverlapping(CreneauHoraire creneauHoraire1, CreneauHoraire creneauHoraire2) {
        return creneauHoraire1.getDate().equals(creneauHoraire2.getDate()) &&
                !(creneauHoraire1.getHeureFin().isBefore(creneauHoraire2.getHeureDebut()) ||
                        creneauHoraire1.getHeureDebut().isAfter(creneauHoraire2.getHeureFin()));
    }

    public List<CreneauHoraire> listCreneauHoraire(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est un chef de cabinet
        if (!user.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF)) {
            throw new RuntimeException("Accès refusé. Seul le chef de cabinet et le dentiste peuvent lister les creneaux horaires.");
        }

        // Retourner tous les dentistes du cabinet du chef
        return creneauHoraireRepository.findByDentisteCabinetId(user.getCabinet().getId());
    }
    public List<CreneauHoraire> getAllCreneaux(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isCabinetChief = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF);
        boolean isDentist = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_DENTIST);

        if (!isCabinetChief && !isDentist) {
            throw new RuntimeException("Accès refusé. Seuls les dentistes et chefs de cabinet peuvent voir les créneaux.");
        }

        // Si c'est un dentiste, il ne voit que ses créneaux
        if (isDentist) {
            Dentiste dentiste = dentisteRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Dentiste non trouvé"));
            return creneauHoraireRepository.findByDentisteId(dentiste.getId());
        }
        // Si c'est un chef de cabinet, il voit tous les créneaux de son cabinet
        else {
            return creneauHoraireRepository.findByDentisteCabinetId(user.getCabinet().getId());
        }
    }

    public List<CreneauHoraire> getCreneauxByStatut(String statut, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isCabinetChief = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_CABINET_CHIEF);
        boolean isDentist = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ROLE_DENTIST);

        if (!isCabinetChief && !isDentist) {
            throw new RuntimeException("Accès refusé.");
        }

        // Si c'est un dentiste
        if (isDentist) {
            Dentiste dentiste = dentisteRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Dentiste non trouvé"));
            return creneauHoraireRepository.findByDentisteIdAndStatut(dentiste.getId(), statut);
        }
        // Si c'est un chef de cabinet
        else {
            return creneauHoraireRepository.findByDentisteCabinetIdAndStatut(user.getCabinet().getId(), statut);
        }
    }

}
