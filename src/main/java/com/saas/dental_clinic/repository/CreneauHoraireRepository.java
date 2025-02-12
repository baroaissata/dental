package com.saas.dental_clinic.repository;

import com.saas.dental_clinic.model.CreneauHoraire;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CreneauHoraireRepository extends CrudRepository<CreneauHoraire, Long> {
    List<CreneauHoraire> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<CreneauHoraire> findByDentisteIdAndDate(Long dentisteId, LocalDate date);
    List<CreneauHoraire> findByDentisteId(Long dentisteId);
    List<CreneauHoraire> findByDentisteCabinetId(Long cabinetId);  // Cette méthode recherche par l'ID du cabinet du dentiste
    List<CreneauHoraire> findByStatut(String statut);
    List<CreneauHoraire> findByDentisteIdAndStatut(Long dentisteId, String statut);
    List<CreneauHoraire> findByDentisteCabinetIdAndStatut(Long cabinetId, String statut);



}
