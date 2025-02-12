package com.saas.dental_clinic.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cabinets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"users"})
public class Cabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Le chef du cabinet
    @OneToOne
    @JoinColumn(name = "chief_id")
    @JsonBackReference
    private User cabinetChief;

    // Tous les utilisateurs associés à ce cabinet
    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    // Champs d'audit
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
        updatedAt = new java.util.Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}