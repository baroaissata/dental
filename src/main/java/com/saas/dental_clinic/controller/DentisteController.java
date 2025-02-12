package com.saas.dental_clinic.controller;


import com.saas.dental_clinic.dto.CreateDentisteRequest;
import com.saas.dental_clinic.model.Dentiste;
import com.saas.dental_clinic.repository.UserRepository;
import com.saas.dental_clinic.service.DentisteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.saas.dental_clinic.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;


@RestController
@RequestMapping("api/dentistes")
public class DentisteController {

    private final DentisteService dentisteService;
    private final UserRepository userRepository;

    public DentisteController(DentisteService dentisteService, UserRepository userRepository) {
        this.dentisteService = dentisteService;
        this.userRepository = userRepository;
    }

//    @PostMapping("/create")
//    public String createDentiste(
//            @RequestBody Dentiste dentiste,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        if (userDetails == null) {
//            throw new RuntimeException("Utilisateur non authentifié !");
//        }
//
//        User loggedInUser = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
//
//        System.out.println("✅ Utilisateur connecté : " + loggedInUser.getEmail());
//
//        return dentisteService.createDentiste(dentiste, loggedInUser);
//    }

    @PostMapping("/create")
    public ResponseEntity<String> createDentiste(@RequestBody CreateDentisteRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        User loggedInUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return ResponseEntity.ok(dentisteService.createDentiste(
                request.getDentiste(),
                request.getPassword(),
                loggedInUser
        ));
    }

    @GetMapping("/list")
    public List<Dentiste> listDentistes(@AuthenticationPrincipal UserDetails userDetails) {
        return dentisteService.listDentistes(userDetails);
    }

    @PutMapping("/{id}")
    public String updateDentiste(
            @PathVariable Long id,
            @RequestBody Dentiste dentiste,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new RuntimeException("Utilisateur non authentifié !");
        }

        System.out.println("✅ Utilisateur connecté : " + userDetails.getUsername());

        try {
            return dentisteService.updateDentiste(id, dentiste, userDetails);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du dentiste : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @DeleteMapping("/{id}")
    public String deleteDentiste(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return dentisteService.deleteDentiste(id, userDetails);
    }

}