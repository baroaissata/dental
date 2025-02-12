package com.saas.dental_clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    // Enum pour définir les types de rôles possibles
    public enum RoleType {
        ROLE_SUPER_ADMIN,
        ROLE_CABINET_CHIEF,
        ROLE_DENTIST,
        ROLE_PATIENT
    }
}