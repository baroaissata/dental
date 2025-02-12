package com.saas.dental_clinic.repository;

import com.saas.dental_clinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Recherche un utilisateur par email (utilisé pour l'authentification)
    Optional<User> findByEmail(String email);

    // Vérifie si un email existe déjà (utile pour la validation)
    boolean existsByEmail(String email);

    // Trouve tous les utilisateurs d'un cabinet spécifique
    @Query("SELECT u FROM User u WHERE u.cabinet.id = :cabinetId")
    List<User> findAllByCabinetId(@Param("cabinetId") Long cabinetId);

    // Trouve tous les dentistes d'un cabinet
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.cabinet.id = :cabinetId AND r.name = 'ROLE_DENTIST'")
    List<User> findAllDentistsByCabinetId(@Param("cabinetId") Long cabinetId);

    // Trouve le chef d'un cabinet
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.cabinet.id = :cabinetId AND r.name = 'ROLE_CABINET_CHIEF'")
    Optional<User> findCabinetChief(@Param("cabinetId") Long cabinetId);
}