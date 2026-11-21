# ADR-002: Comunicación entre servicios mediante REST/HTTP

**Estado:** Propuesta para validar con el equipo

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

Por ahora se propone utilizar **REST sobre HTTP con comunicación síncrona** para la consulta de eventos.

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

Se ejecutaron ambos servicios de forma independiente y se realizaron peticiones HTTP desde PowerShell mediante `Invoke-RestMethod`.

En la prueba se confirmó que el proveedor respondió con información JSON y que el consumidor recuperó y utilizó los campos del evento.