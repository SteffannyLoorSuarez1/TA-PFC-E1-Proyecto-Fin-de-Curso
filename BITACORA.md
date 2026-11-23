# Bitácora del equipo SREI
## Steffanny Loor — Desarrollo y comunicación entre servicios

### Sesión del 20 de noviembre de 2026

**Actividad realizada:** Desarrollé los dos servicios iniciales del SREI utilizando Java y Spring Boot. El primero, `servicio-eventos`, proporciona información de prueba sobre eventos interculturales, mientras que `servicio-consulta` recupera esos datos mediante una petición REST/HTTP. Ambos servicios funcionan como procesos independientes en los puertos 8080 y 8081.

**Decisión tomada:** Elegí REST/HTTP con comunicación síncrona para la interacción inicial entre servicios, considerando su compatibilidad con Spring Boot y la facilidad para comprobar las respuestas. Documenté esta decisión en `ADR-002-comunicacion.md`.

**Resultado:** Conseguí que el consumidor consultara al proveedor y utilizara los datos recibidos para construir su propia respuesta.

### Sesión del 22 de noviembre de 2026

**Actividad realizada:** Continué con las pruebas de comunicación y mejoré el manejo de errores del servicio consumidor.

**Problema encontrado:** Cuando el proveedor no estaba disponible era necesario controlar los tiempos de espera y evitar que el consumidor dejara la solicitud sin una respuesta adecuada.

**Solución aplicada:** Configuré tiempos de espera de 500 ms para establecer la conexión y 1000 ms para recibir la respuesta, también implementé el manejo del error HTTP 503 cuando el proveedor no está disponible.

**Verificación:** Ejecuté pruebas con ambos servicios funcionando y con el proveedor apagado. Realicé cinco solicitudes de comprobación de indisponibilidad, todas devolvieron HTTP 503, con tiempos inferiores a 2000 ms.

**Evidencias:** Organicé cinco capturas en `evidencias/ejecucion/`, donde se muestran el arranque de los servicios, las respuestas HTTP 200, la comunicación REST y el manejo del error HTTP 503. También actualicé el ADR-002 con las verificaciones realizadas.