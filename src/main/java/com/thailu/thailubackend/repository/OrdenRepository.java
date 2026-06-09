package com.thailu.thailubackend.repository;

import com.thailu.thailubackend.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
Optional<Orden> findByCodigoSeguimiento(String codigoSeguimiento);
}