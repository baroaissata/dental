package com.saas.dental_clinic.controller;


import com.saas.dental_clinic.model.CreneauHoraire;
import com.saas.dental_clinic.model.Dentiste;
import com.saas.dental_clinic.service.CreneauHoraireService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/creneaux")
public class CreneauHoraireController {
    private final CreneauHoraireService creneauHoraireService;

    public CreneauHoraireController(CreneauHoraireService creneauHoraireService) {
        this.creneauHoraireService = creneauHoraireService;
    }
    @GetMapping("/list")
    public List<CreneauHoraire> listCreneauHoraire(@AuthenticationPrincipal UserDetails userDetails) {
        return creneauHoraireService.listCreneauHoraire(userDetails);
    }

    @PostMapping("/create")
    public ResponseEntity<CreneauHoraire> createCreneauHoraire(
            @RequestBody CreneauHoraire creneauHoraire,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creneauHoraireService.createCreneauHoraire(creneauHoraire, userDetails));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CreneauHoraire> updateTimeSlot(
            @PathVariable Long id,
            @RequestBody CreneauHoraire creneauHoraire,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creneauHoraireService.updateCreneauHoraire(id, creneauHoraire, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreneauHoraire(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        creneauHoraireService.deleteCreneauHoraire(id, userDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dentiste/{dentisteId}")
    public ResponseEntity<List<CreneauHoraire>> getCreneauHoraireByDentist(
            @PathVariable Long dentisteId) {
        return ResponseEntity.ok(creneauHoraireService.getCreneauHoraireByDentist(dentisteId));
    }
    @GetMapping
    public ResponseEntity<List<CreneauHoraire>> getAllCreneaux(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creneauHoraireService.getAllCreneaux(userDetails));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<CreneauHoraire>> getCreneauxByStatut(
            @PathVariable String statut,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(creneauHoraireService.getCreneauxByStatut(statut, userDetails));
    }

}
