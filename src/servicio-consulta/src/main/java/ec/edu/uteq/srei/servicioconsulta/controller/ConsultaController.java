package ec.edu.uteq.srei.servicioconsulta.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final RestClient cliente;
    private final String urlProveedor;

    public ConsultaController(
            @Value("${servicio.eventos.url}") String urlProveedor,
            @Value("${servicio.eventos.timeout-conexion-ms}") int timeoutConexion,
            @Value("${servicio.eventos.timeout-respuesta-ms}") int timeoutRespuesta) {
        this.urlProveedor = urlProveedor;

        SimpleClientHttpRequestFactory fabrica =
                new SimpleClientHttpRequestFactory();

        fabrica.setConnectTimeout(Duration.ofMillis(timeoutConexion));
        fabrica.setReadTimeout(Duration.ofMillis(timeoutRespuesta));

        this.cliente = RestClient.builder()
                .baseUrl(urlProveedor)
                .requestFactory(fabrica)
                .build();
    }

    @GetMapping(value = "/evento", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> consultarEvento() {

        Map<String, Object> evento;

        try {
            evento = cliente.get()
                    .uri("/api/eventos/prueba")
                    .retrieve()
                    .body(Map.class);

        } catch (RestClientException excepcion) {
            Map<String, Object> error = new LinkedHashMap<>();

            error.put("mensaje", "El servicio de eventos no esta disponible");
            error.put("estado", 503);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error);
        }

        if (evento == null) {
            Map<String, Object> error = new LinkedHashMap<>();

            error.put("mensaje", "El proveedor no devolvio informacion");
            error.put("estado", 503);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error);
        }
        Map<String, Object> resultado = new LinkedHashMap<>();


        resultado.put("servicio", "servicio-consulta");
        resultado.put("mensaje", "Evento recibido del proveedor");
        resultado.put("nombreEvento", evento.get("nombre"));
        resultado.put("estadoEvento", evento.get("estado"));
        resultado.put("datoPrueba", evento.get("datoPrueba"));

        return ResponseEntity.ok(resultado);
    }
}