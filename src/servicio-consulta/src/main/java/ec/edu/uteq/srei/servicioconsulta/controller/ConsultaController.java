package ec.edu.uteq.srei.servicioconsulta.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final RestClient cliente;

    public ConsultaController(
            @Value("${servicio.eventos.url}") String urlProveedor) {

        this.cliente = RestClient.builder()
                .baseUrl(urlProveedor)
                .build();
    }

    @GetMapping(value = "/evento", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> consultarEvento() {

        Map<String, Object> evento = cliente.get()
                .uri("/api/eventos/prueba")
                .retrieve()
                .body(Map.class);

        if (evento == null) {
            throw new IllegalStateException(
                    "El proveedor no devolvio informacion");
        }

        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put("servicio", "servicio-consulta");
        resultado.put("mensaje", "Evento recibido del proveedor");
        resultado.put("nombreEvento", evento.get("nombre"));
        resultado.put("estadoEvento", evento.get("estado"));
        resultado.put("datoPrueba", evento.get("datoPrueba"));

        return resultado;
    }
}