# ADR-001: Estilo de arquitectura de SREI

- Estado: Propuesto
- Fecha: 2026-11-20
- Responsable: Washington Apunte (Arquitecto)

## Contexto
SREI es una aplicación web cliente-servidor organizada en capas: un frontend Angular 17 (SPA) usa la API REST de un único backend Spring Boot (Java 17), que trabaja con una sola base de datos PostgreSQL.
Ese backend concentra toda la lógica de negocio (eventos, propuestas, inscripciones, usuarios, sesiones y estadísticas), la seguridad con JWT y el almacenamiento de imágenes y PDF en el disco del servidor.
El acceso se controla por roles (RBAC): decano, coordinador, docente y asistente (estudiante o usuario externo), cada uno con sus propias vistas y permisos.
El PFC exige que SREI sea un sistema distribuido, pero hoy todo corre en un solo backend con una sola base de datos, así que hay que decidir qué estilo de arquitectura adoptar.

## Opciones consideradas
pendiente

## Decisión
pendiente

## Consecuencias
pendiente