# SREI — Sistema de Registro de Eventos Interculturales

Repositorio del Proyecto Fin de Curso de Aplicaciones Distribuidas.
Primera entrega: TA-PFC-E1.
## Descripción

El Sistema de Registro de Eventos Interculturales (SREI) está orientado al registro y consulta de eventos interculturales. Para la esta entrega se desarrolló un prototipo con dos servicios independientes que intercambian información mediante REST sobre HTTP.

## Servicios implementado
| Servicio | Carpeta | Puerto | Función |
|---|---|---|---|
| Servicio de eventos | `src/servicio-eventos/` | 8080 | Proporciona información de un evento de prueba. |
| Servicio de consulta | `src/servicio-consulta/` | 8081 | Consulta al servicio de eventos y utiliza su respuesta. |

La comunicación es síncrona por lo que el servicio de consulta realiza una petición HTTP y espera la respuesta del servicio de eventos.

## Requisitos para ejecutar el prototipo
- Java 21.
- Una terminal PowerShell en Windows.
- Conexión a Internet para descargar las dependencias de Maven durante la primera ejecución.
- Puertos 8080 y 8081 disponibles.
Cada servicio incluye Maven Wrapper (`mvnw.cmd`), por lo que no es necesario instalar Maven por separado.
## Ejecución

Los servicios deben ejecutarse en dos terminales diferentes.

### 1. Iniciar el servicio de eventos

Desde la raíz del repositorio:

```powershell
cd src/servicio-eventos
.\mvnw.cmd spring-boot:run
```

El servicio debe iniciar en el puerto 8080.

### 2. Iniciar el servicio de consulta

Abrir otra terminal y, desde la raíz del repositorio, ejecuta:

```powershell
cd src/servicio-consulta
.\mvnw.cmd spring-boot:run
```

El servicio debe iniciar en el puerto 8081.

Ambas terminales deben mantenerse abiertas mientras realizas las pruebas.

## Comprobación de funcionamiento

Abrir una tercera terminal PowerShell.

### Consultar directamente al proveedor

```powershell
Invoke-RestMethod "http://localhost:8080/api/eventos/prueba" | ConvertTo-Json
```

La respuesta contiene un evento de prueba con los siguientes datos:

- ID: `1`
- Nombre: `Festival Intercultural de Prueba`
- Ubicación: `Quevedo`
- Estado: `PROGRAMADO`
- `datoPrueba`: `true`

### Comprobar la comunicación entre servicios

```powershell
Invoke-RestMethod "http://localhost:8081/api/consultas/evento" | ConvertTo-Json
```

Respuesta esperada:

```json
{
  "servicio": "servicio-consulta",
  "mensaje": "Evento recibido del proveedor",
  "nombreEvento": "Festival Intercultural de Prueba",
  "estadoEvento": "PROGRAMADO",
  "datoPrueba": true
}
```
Esta segunda prueba permite comprobar que el servicio de consulta obtiene información del servicio de eventos y la utiliza para construir su respuesta.

## Comunicación y arquitectura

La comunicación implementada es:

**Servicio de consulta → Servicio de eventos — Consulta de eventos por REST/HTTP (síncrona).**

La decisión y sus alternativas se documentan en `docs/adr/ADR-002-comunicacion.md`.

## Alcance actual

Esta implementación corresponde al esqueleto funcional de la primera entrega. Esta no es la versión completa del sistema.

Los registros utilizados son de prueba y no provienen de usuarios reales.