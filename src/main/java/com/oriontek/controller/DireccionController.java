package com.oriontek.controller;

import com.oriontek.model.Cliente;
import com.oriontek.model.Direccion;
import com.oriontek.repository.ClienteRepository;
import com.oriontek.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todas las direcciones de un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getDireccionesByCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(generarError(HttpStatus.NOT_FOUND, "Cliente con ID " + clienteId + " no encontrado."));
        }
        List<Direccion> direcciones = direccionRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(direcciones);
    }

    // Crear una dirección para un cliente
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<?> createDireccion(@PathVariable Long clienteId, @RequestBody Direccion direccion) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(generarError(HttpStatus.NOT_FOUND, "Cliente con ID " + clienteId + " no encontrado."));
        }

        Cliente cliente = clienteOpt.get();
        direccion.setCliente(cliente);

        Optional<Direccion> direccionExistente = direccionRepository.findByClienteIdAndDireccion(clienteId, direccion.getDireccion());
        if (direccionExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(generarError(HttpStatus.BAD_REQUEST, "La dirección ya está asociada al cliente."));
        }

        // Guardamos la nueva dirección
        Direccion nuevaDireccion = direccionRepository.save(direccion);

        // Retornamos la dirección creada
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDireccion);
    }


    // Eliminar una dirección por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDireccion(@PathVariable Long id) {
        if (!direccionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(generarError(HttpStatus.NOT_FOUND, "Dirección con ID " + id + " no encontrada."));
        }
        direccionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "status", HttpStatus.OK.value(),
                "message", "Dirección con ID " + id + " eliminada exitosamente."
        ));
    }

    // Método para generar errores
    private HashMap<String, Object> generarError(HttpStatus status, String mensaje) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", mensaje);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
