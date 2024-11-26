package com.oriontek.repository;

import com.oriontek.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByClienteId(Long clienteId);

    Optional<Direccion> findByClienteIdAndDireccion(Long clienteId, String direccion);

}

