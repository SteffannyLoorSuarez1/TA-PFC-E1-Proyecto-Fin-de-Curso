# SREI — Sistema de Registro de Eventos Interculturales

Repositorio del Proyecto Fin de Curso (PFC) de Aplicaciones Distribuidas.
Primera entrega: **TA-PFC-E1 — Propuesta y análisis del sistema distribuido**.

## Datos de la entrega

| Campo | Detalle |
|---|---|
| Universidad | Universidad Técnica Estatal de Quevedo (UTEQ) |
| Facultad | Ciencias de la Computación y Diseño Digital |
| Carrera | Software |
| Asignatura | Aplicaciones Distribuidas (ISR-701) |
| Docente | Ing. Gleiston Cicerón Guerrero Ulloa, PhD |
| Periodo académico | SPA 2026–2027 |
| Nivel y paralelo | Séptimo nivel, paralelo B |
| Grupo | ALL |
| Fecha de entrega | 22 de noviembre de 2026 |

## Video de la entrega

Enlace al video: **PEGAR_AQUI_EL_ENLACE_DEL_VIDEO**

## Equipo

| Integrante | Rol | Nombre que aparece en los commits |
|---|---|---|
| Washington Germán Apunte Pazmiño | Arquitecto y Responsable de Calidad | `Washinton-Apunte` |
| Jordan Josue Loor Suárez | Responsable de Requisitos y Documentación | `jloors13` |
| Steffanny del Rocío Loor Suárez | Líder de Desarrollo | `STEFFANNY DEL ROCIO LOOR SUAREZ` |

## Descripción

El Sistema de Registro de Eventos Interculturales (SREI) está orientado al registro y consulta de eventos interculturales. El SREI original es una aplicación web en capas: un frontend Angular 21 y un único backend Spring Boot con una base de datos PostgreSQL. En este proyecto se reorganiza como un sistema distribuido.

Para esta entrega se desarrolló un prototipo con dos servicios independientes que intercambian información mediante REST sobre HTTP.

## Arquitectura

La decisión está en [ADR-001](docs/adr/ADR-001-estilo.md): SREI se divide en tres servicios Spring Boot con Java 21, y cada uno guarda sus datos en su propia base PostgreSQL, sin entrar a la base de otro. La aplicación web en Angular 21 llama a los servicios por REST/HTTPS. Entre servicios la comunicación es REST/HTTP síncrona, como se decide en [ADR-002](docs/adr/ADR-002-comunicacion.md). La generación de imágenes depende de un servicio de IA externo.

| Contenedor | Puerto | Qué hace | Estado en E1 |
|---|---|---|---|
| servicio-eventos | 8080 | Maneja los eventos y su estado | Implementado, con datos de prueba |
| servicio-consulta | 8081 | Atiende las consultas y le pide los datos a servicio-eventos. En E2 pasa a manejar las inscripciones | Implementado, con datos de prueba |
| servicio-usuarios | — | Usuarios, roles e inicio de sesión | Previsto |
| Aplicación web (Angular 21) | — | Interfaz para los usuarios | Prevista (INT-04 a INT-06) |
| Bases PostgreSQL | — | Una por servicio | Previstas (INT-07 a INT-09) |
| Servicio de IA | — | Generación de imágenes (externo) | Previsto (INT-03) |

Los diagramas C4 están en `docs/diagramas/`: [contexto](docs/diagramas/c4-contexto.pdf) (nivel 1) y [contenedores](docs/diagramas/c4-contenedores.pdf) (nivel 2). En el de contenedores, las líneas continuas son lo implementado y las discontinuas lo previsto.

### Integraciones

| ID | Origen → destino | Protocolo | Estado |
|---|---|---|---|
| INT-01 | servicio-consulta → servicio-eventos | REST/HTTP, síncrona | Implementado y probado |
| INT-02 | servicio-consulta → servicio-usuarios | REST/HTTP | Previsto |
| INT-03 | servicio-eventos → servicio de IA | HTTPS/REST | Previsto |
| INT-04 | Aplicación web → servicio-eventos | REST/HTTPS | Previsto |
| INT-05 | Aplicación web → servicio-consulta | REST/HTTPS | Previsto |
| INT-06 | Aplicación web → servicio-usuarios | REST/HTTPS | Previsto |
| INT-07 | servicio-eventos → base de eventos | JDBC | Previsto |
| INT-08 | servicio-consulta → base de inscripciones | JDBC | Previsto |
| INT-09 | servicio-usuarios → base de usuarios | JDBC | Previsto |

El detalle de cada integración está en la sección 4 del informe.

## Servicios implementados

| Servicio | Carpeta | Puerto | Función |
|---|---|---|---|
| Servicio de eventos | `src/servicio-eventos/` | 8080 | Proporciona información de un evento de prueba. |
| Servicio de consulta | `src/servicio-consulta/` | 8081 | Consulta al servicio de eventos y utiliza su respuesta. |

Ambos usan Spring Boot 4.1.3 y Java 21. La comunicación es síncrona, por lo que el servicio de consulta realiza una petición HTTP y espera la respuesta del servicio de eventos.

## Requisitos para ejecutar el prototipo

- Java 21 (JDK).
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

Abrir otra terminal y, desde la raíz del repositorio, ejecutar:

```powershell
cd src/servicio-consulta
.\mvnw.cmd spring-boot:run
```

El servicio debe iniciar en el puerto 8081.

Ambas terminales deben mantenerse abiertas mientras se realizan las pruebas. En Linux o macOS se usa `sh mvnw spring-boot:run` dentro de cada carpeta.

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

## Solución de problemas

**`release version 21 not supported` al compilar.** Maven está usando un JDK anterior, por ejemplo Java 17 configurado en `JAVA_HOME`. En la terminal donde se va a iniciar el servicio, apuntar `JAVA_HOME` a un JDK 21 y comprobarlo:

```powershell
$env:JAVA_HOME = "C:\ruta\al\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

El cambio solo vale para esa terminal, así que hay que repetirlo en cada terminal nueva.

**`Port 8080 was already in use`.** Quedó abierto un servicio de una ejecución anterior. Cerrar esa terminal, o buscar el proceso y detenerlo:

```powershell
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
Stop-Process -Id <PID>
```

## Resultados de las pruebas de RNF

Se midieron tres requisitos no funcionales sobre el prototipo, en local, el 22/11/2026, con PowerShell 5.1, Java 21.0.13 y Spring Boot 4.1.3. El prototipo solo implementa INT-01, con una respuesta mínima y sin base de datos, así que estos tiempos no representan al sistema completo. El método de cada prueba y sus umbrales están en la sección 3 del informe, y las capturas en `evidencias/ejecucion/`.

| RNF | Resultado | Estado | Evidencia |
|---|---|---|---|
| RNF-01 Comportamiento temporal | 30 peticiones GET a `/api/consultas/evento`: promedio 0.0046 s y máximo 0.0148 s (umbral: máximo 1.0 s) | Cumple | `washington-04` |
| RNF-06 Modularidad | servicio-eventos responde solo en el puerto 8080, y servicio-consulta arranca en el 8081 con servicio-eventos apagado | Cumple | `washington-05`, `washington-06`, `washington-07` |
| RNF-02 Tolerancia a fallos | Con servicio-eventos apagado, las 5 peticiones a `/api/consultas/evento` reciben un error 500; servicio-consulta sigue respondiendo | No se cumple todavía: falta devolver 503 con un mensaje claro (riesgo R-04) | `washington-07` |

RNF-03, RNF-04 y RNF-05 dependen de la aplicación web y de servicio-usuarios, así que se medirán cuando existan.

## Estructura del repositorio

```text
.
├── README.md
├── BITACORA.md                     Bitácora del equipo
├── docs/
│   ├── main.tex, main.pdf          Informe de la entrega (fuente LaTeX y PDF)
│   ├── secciones/                  Secciones 01 a 05 del informe
│   ├── adr/                        ADR-001 y ADR-002
│   ├── diagramas/                  Diagramas C4 en .drawio y .pdf
│   └── referencias/                Bibliografía BibTeX (estilo IEEE)
├── evidencias/
│   ├── levantamiento/entrevistas/  Grabaciones de entrevistas y validaciones
│   └── ejecucion/                  Capturas de ejecución y de las pruebas de RNF
└── src/
    ├── servicio-eventos/           Proveedor REST (puerto 8080)
    └── servicio-consulta/          Consumidor REST (puerto 8081)
```

## Informe

El informe completo es [`docs/main.pdf`](docs/main.pdf). Sus secciones están en `docs/secciones/`:

1. `01-contexto.tex`: contexto y antecedentes del SREI.
2. `02-requisitos.tex`: historias de usuario y requisitos.
3. `03-arquitectura.tex`: diagramas C4, requisitos no funcionales, CAP/PACELC, riesgos y resultados de las mediciones.
4. `04-comunicacion.tex`: integraciones INT-01 a INT-09 y la implementación de INT-01.
5. `05-plan-aportes.tex`: roles, responsabilidades y aportes de cada integrante.

Para volver a compilarlo hace falta una distribución LaTeX (MiKTeX o TeX Live) con `biblatex` y `biber`. Desde la carpeta `docs/`:

```powershell
pdflatex main
biber main
pdflatex main
pdflatex main
```

## Evidencias

- `evidencias/levantamiento/entrevistas/`: tres entrevistas grabadas (coordinadora de la carrera de Software, decano de la FCCDD y una docente de la Facultad de Ciencias de la Educación) y dos sesiones de validación de requisitos, con la coordinadora y con el decano. El README de esa carpeta lista cada archivo.
- `evidencias/ejecucion/`: capturas `01` a `03` con la respuesta del proveedor, la del consumidor y los servicios iniciados; capturas `washington-01` a `washington-07` con el arranque de los servicios, la prueba de INT-01 y las mediciones de RNF-01, RNF-02 y RNF-06.

## Alcance actual

Esta implementación corresponde al esqueleto funcional de la primera entrega. No es la versión completa del sistema: todavía no hay bases de datos, servicio-usuarios ni conexión con la aplicación web.

Los registros utilizados son de prueba y no provienen de usuarios reales.

## Forma de trabajo con Git

Cada integrante trabaja en sus propias ramas (`washington/...`, `jordan/...`, `steffanny/...`). Los cambios llegan a `main` por pull request con *merge commit*, para que el historial conserve quién hizo cada commit.

## Uso de inteligencia artificial

El uso de herramientas de IA en cada parte del trabajo se declara en el informe (`docs/main.pdf`). 