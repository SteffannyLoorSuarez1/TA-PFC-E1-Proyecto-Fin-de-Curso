package ec.edu.uteq.srei.servicioeventos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
     @GetMapping("/prueba")
    public Map<String, Object> obtenerEventoPrueba() {
        return Map.of(
            "id", 1,
            "nombre", "Festival Intercultural de Prueba",
            "ubicacion", "Quevedo",
            "estado", "PROGRAMADO",
            "datoPrueba", true
        );
    }
}
