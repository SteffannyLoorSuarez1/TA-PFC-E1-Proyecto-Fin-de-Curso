# ADR-002: Comunicación entre servicios mediante REST/HTTP

**Estado:** Aceptado

**Fecha:** 20 de noviembre de 2026

**Proyecto:** SREI — Sistema de Registro de Eventos Interculturales

## 1. Contexto

El proyecto de Sistema de Registro de eventos Interculturales necesita que sus servicios intercambien información sin depender de una única aplicación. Para la primera entrega se desarrolló dos servicios independientes: uno encargado de proporcionar información de eventos y otro que permite consultarla.

El servicio de consulta necesita obtener los datos del servicio de eventos y utilizarlos para responder a una solicitud. Por esta razón se requiere un mecanismo de comunicación que sea sencillo de implementar, probar y documentar.

## 2. Alternativas consideradas

### REST sobre HTTP

Permite intercambiar información mediante peticiones HTTP y respuestas JSON. Es compatible con Spring Boot y facilita comprobar los resultados desde una terminal.

### gRPC

Permite la comunicación entre servicios mediante contratos definidos con Protocol Buffers. Es una alternativa útil cuando se necesita un contrato estricto y una comunicación eficiente pero este requiere configuración y herramientas adicionales.

### Mensajería asíncrona

La mensajería asíncrona permite intercambiar mensajes mediante un intermediario sin que el emisor tenga que esperar necesariamente una respuesta inmediata, lo cual puede ser útil para notificaciones y procesamiento de eventos, aunque agrega componentes que no son necesarios para la consulta inicial.

## 3. Decisión

Se decidió utilizar **REST sobre HTTP con comunicación síncrona** para la consulta de eventos. Esta decisión fue implementada y comprobada mediante la interacción INT-01 entre el servicio de consulta y el servicio de eventos.

El servicio consumidor realiza una petición HTTP al servicio proveedor y espera su respuesta antes de continuar. Los datos se intercambian en formato JSON.

La relación implementada es:

**Servicio de consulta → Servicio de eventos — Consulta de eventos por REST/HTTP (síncrona).**

En el prototipo se utiliza la siguiente interacción:

- Proveedor: `GET http://localhost:8080/api/eventos/prueba`
- Consumidor: `GET http://localhost:8081/api/consultas/evento`

El servicio de eventos entrega un registro identificado como dato de prueba. El servicio de consulta recibe esa información y utiliza algunos de sus campos para construir su propia respuesta.

## 4. Consecuencias

### Ventajas

- Los servicios pueden ejecutarse como procesos independientes.
- Las peticiones y respuestas son fáciles de comprobar.
- Se utilizan mecanismos compatibles con Spring Boot.
- La comunicación puede documentarse claramente en el diagrama C4.

### Limitaciones

- El consumidor depende de que el proveedor esté disponible cuando realiza la consulta.
- Una falla de red o del proveedor puede impedir que el consumidor pueda completar la operación.
- La comunicación síncrona introduce tiempo de espera durante la solicitud.
- Este prototipo todavía utiliza datos de prueba y no representa una implementación completa del sistema.

## 5. Verificación

Se ejecutaron los servicios `servicio-eventos` y `servicio-consulta` como procesos independientes utilizando los puertos 8080 y 8081, respectivamente.

Se comprobó mediante solicitudes HTTP que el proveedor devuelve un evento de prueba en formato JSON con código HTTP 200. También se verificó que el consumidor consulta al proveedor mediante REST/HTTP síncrono (INT-01), recupera los datos del evento y los utiliza para construir su propia respuesta HTTP 200.

Para comprobar el comportamiento en caso de la indisponibilidad del proveedor (RNF-02) se detuvo `servicio-eventos` y se mantuvo funcionando `servicio-consulta`, se configuraron tiempos de espera de 500 ms para la conexión y 1000 ms para la lectura.

Se realizaron cinco solicitudes al consumidor con el proveedor apagado. Todas devolvieron HTTP 503 y un mensaje JSON indicando que el servicio de eventos no estaba disponible.

| Prueba | Código HTTP | Tiempo de respuesta |
|---|---|---|
| 1 | 503 | 58,45 ms |
| 2 | 503 | 31,54 ms |
| 3 | 503 | 28,97 ms |
| 4 | 503 | 27,60 ms |
| 5 | 503 | 31,79 ms |

El tiempo máximo observado fue de 58,45 ms, inferior al umbral de 2000 ms establecido para esta prueba. Estos resultados corresponden a una ejecución local con el proveedor apagado y no garantizan el mismo tiempo en otros escenarios de fallo.

Las evidencias se encuentran en `evidencias/ejecucion/`, en las capturas identificadas con el prefijo `steffanny-`.