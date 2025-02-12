package com.saas.dental_clinic.repository;

import com.saas.dental_clinic.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Cette méthode permet de trouver un rôle par son nom (ex: ROLE_SUPER_ADMIN)
    // Optional est utilisé pour gérer proprement le cas où le rôle n'existe pas
    Optional<Role> findByName(Role.RoleType name);

    // Vérifie si un rôle existe déjà dans la base de données
    boolean existsByName(Role.RoleType name);
}