package com.saas.dental_clinic.repository;

import com.saas.dental_clinic.model.Cabinet;
import com.saas.dental_clinic.model.Dentiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DentisteRepository extends JpaRepository<Dentiste, Long> {
    List<Dentiste> findByCabinet(Cabinet cabinet);
    Optional<Dentiste> findByEmail(String email);

}
