package com.oriontek.controller;

import com.oriontek.model.Cliente;
import com.oriontek.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todos los clientes
    @GetMapping
    public ResponseEntity<?> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "status", HttpStatus.OK.value(),
                            "message", "No hay clientes disponibles.",
                            "clientes", clientes
                    ));
        }
        return ResponseEntity.ok(clientes);
    }


    // Crear un cliente
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(generarError(HttpStatus.BAD_REQUEST, "Error al crear el cliente: " + e.getMessage()));
        }
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError(HttpStatus.NOT_FOUND, "Cliente con ID " + id + " no encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el cliente: " + e.getMessage()));
        }
    }

    // Eliminar cliente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClienteById(@PathVariable Long id) {
        try {
            if (clienteRepository.existsById(id)) {
                clienteRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("status", HttpStatus.OK.value(), "message", "Cliente con ID " + id + " eliminado exitosamente."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError(HttpStatus.NOT_FOUND, "Cliente con ID " + id + " no encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el cliente: " + e.getMessage()));
        }
    }

    // Método para generar errores con código de estado y mensaje
    private Map<String, Object> generarError(HttpStatus status, String mensaje) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", mensaje);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }
}
