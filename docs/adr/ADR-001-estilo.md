# ADR-001: Estilo de arquitectura de SREI

- Estado: Aceptado
- Fecha: 2026-11-21
- Responsable: Washington Apunte (Arquitecto)

## Contexto
SREI es una aplicación web cliente-servidor organizada en capas: un frontend Angular 21 (SPA) usa la API REST de un único backend Spring Boot (Java 17), que trabaja con una sola base de datos PostgreSQL.
Ese backend concentra toda la lógica de negocio (eventos, propuestas, inscripciones, usuarios, sesiones y estadísticas), la seguridad con JWT y el almacenamiento de imágenes y PDF en el disco del servidor.
El acceso se controla por roles (RBAC): decano, coordinador, docente y asistente (estudiante o usuario externo), cada uno con sus propias vistas y permisos.
El PFC exige que SREI sea un sistema distribuido, pero hoy todo corre en un solo backend con una sola base de datos, así que hay que decidir qué estilo de arquitectura adoptar.

## Opciones consideradas

### Opción A: Monolito en capas (como está hoy)
Es lo que SREI tiene ahora: un solo backend Spring Boot con una sola base PostgreSQL.
- A favor: Ya funciona y el equipo lo conoce. Se despliega todo de una sola vez.
- En contra: No es distribuido. Todo corre en el mismo servidor, así que un problema en una parte afecta a todas. Por ejemplo, generar una imagen depende del servicio de IA externo. Si la IA tarda en responder, esas peticiones se quedan esperando en el mismo servidor que atiende el login y las inscripciones. Si se juntan muchas, el servidor se queda sin hilos libres y todo SREI se pone lento.
- Resultado: descartada, porque no cumple lo que pide el PFC.

### Opción B: Monolito modular
Sigue siendo un solo backend, pero ordenado por dentro en módulos: eventos, inscripciones y usuarios.
- A favor: El código queda más ordenado y no hay llamadas por red.
- En contra: Sigue siendo un solo proceso. Si se cae, se cae todo, igual que hoy.
- Resultado: descartada, porque sigue siendo un solo despliegue con una sola base, así que no es distribuido. Igual nos sirvió para decidir cómo partir los servicios.

### Opción C: Servicios independientes con base de datos propia
Cada parte de SREI es un servicio Spring Boot aparte, con su propia base PostgreSQL. Los servicios se hablan por REST/HTTP.
- A favor:
  - Es un sistema distribuido, que es lo que pide el PFC.
  - Un problema en un servicio no tumba a los demás. Con el ejemplo de la IA: la llamada solo sale de servicio-eventos (INT-03, previsto). Si la IA tarda, servicio-usuarios sigue igual. servicio-consulta también sigue funcionando, y solo se afecta cuando le pide un evento a servicio-eventos (INT-01).
  - Seguimos usando Angular, Spring Boot y PostgreSQL, que ya conocemos.
  - Ya probamos la idea: servicio-consulta le pide un evento a servicio-eventos y funciona (INT-01).
- En contra:
  - Hay más cosas que levantar: tres servicios, cada uno con su puerto y su base.
  - Ya no se puede hacer un JOIN entre bases de distintos servicios. Esos datos se piden por la API.
  - Si servicio-eventos se cae, servicio-consulta no puede responder la consulta.
- Resultado: elegida, porque es la única opción que cumple el PFC sin cambiar de tecnologías.

## Decisión
Elegimos la opción C. SREI se divide en tres servicios Spring Boot con Java 21, que es la versión que usa el esqueleto de E1:
- servicio-eventos (puerto 8080): maneja los eventos y su estado.
- servicio-consulta (puerto 8081): atiende las consultas y le pide los datos a servicio-eventos. En E2 pasa a manejar las inscripciones.
- servicio-usuarios (previsto): maneja usuarios, roles y el inicio de sesión.

Cada servicio tiene su propia base PostgreSQL y ninguno entra a la base de otro. La aplicación web en Angular 21 llama a los servicios por REST/HTTPS. Entre servicios se usa REST/HTTP síncrono, como dice el ADR-002. El servicio de IA sigue siendo externo.

Los nombres, puertos e IDs (INT-01 a INT-09) son los del issue #5 y del diagrama `c4-contenedores`. Por ahora solo están hechos servicio-eventos, servicio-consulta y el enlace INT-01. Lo demás está previsto.

## Consecuencias
Positivas:
- SREI cumple el requisito de sistema distribuido del PFC.
- Cada servicio se puede reiniciar sin apagar los otros.
- Cada servicio se prueba solo. Por ejemplo, servicio-eventos responde en el puerto 8080 aunque los demás estén apagados.

Negativas:
- Para probar todo el sistema hay que levantar varios servicios, cada uno con sus pasos en el README.
- Los datos de distintos servicios no se pueden unir con un JOIN. Hay que pedirlos por la API, y por un momento pueden no coincidir entre servicios. Esto se explica en la parte de CAP/PACELC de 03-arquitectura.
- Como la comunicación es síncrona, si servicio-eventos se cae, la consulta falla y el usuario ve un error.

Seguimiento:
- Implementar servicio-usuarios y las tres bases de datos.
- Definir un tiempo de espera y un mensaje de error claro para cuando un servicio no responde.
- Mantener los mismos IDs en el diagrama, el issue #5 y la tabla de 04-comunicacion.tex.