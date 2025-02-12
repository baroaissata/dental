package com.saas.dental_clinic.repository;

import com.saas.dental_clinic.model.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface CabinetRepository extends JpaRepository<Cabinet, Long> {
    // Trouve un cabinet par son nom
    Optional<Cabinet> findByName(String name);

    // Vérifie si un cabinet existe déjà avec ce nom
    boolean existsByName(String name);

    // Trouve tous les cabinets gérés par un chef spécifique
    @Query("SELECT c FROM Cabinet c WHERE c.cabinetChief.id = :chiefId")
    Optional<Cabinet> findByChiefId(@Param("chiefId") Long chiefId);

    // Recherche de cabinets par nom (utile pour la recherche)
    @Query("SELECT c FROM Cabinet c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Cabinet> searchByName(@Param("searchTerm") String searchTerm);
}