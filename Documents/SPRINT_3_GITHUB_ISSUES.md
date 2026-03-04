# Sprint 3 — Lista de incidencias para GitHub

Este sprint implementa la **Fase 3** de la hoja de ruta: cobertura de pruebas automatizadas, mejora de calidad continua y documentación operativa.

## Objetivo del sprint

Elevar la calidad técnica del producto mediante pruebas de dominio, limpieza de configuración y documentación de operación por entorno.

## Duración sugerida

- 2 semanas

## Estructura recomendada en GitHub

- Hito: `Sprint 3 - Calidad continua y documentación`
- Etiquetas sugeridas:
  - `sprint-3`
  - `calidad`
  - `pruebas`
  - `deuda-tecnica`
  - `documentacion`
  - `prioridad:media`

---

## Incidencia 1: Pruebas de expiración y renovación de token

**Título sugerido:** `Pruebas: cubrir lógica de expiración y renovación de token`

**Tipo:** Pruebas / Seguridad

**Descripción:**
La lógica de sesión debe estar respaldada con pruebas para reducir regresiones en autenticación.

**Tareas:**
- Crear pruebas unitarias para validación de expiración de token.
- Simular escenarios de renovación exitosa y fallida.
- Validar persistencia segura de token en flujo de renovación.
- Documentar casos borde relevantes.

**Criterios de aceptación:**
- Cobertura de casos de token válido, vencido y malformado.
- Renovación validada con resultados esperados.
- No se introducen regresiones en login/logout.

**Estimación:** 5 puntos

---

## Incidencia 2: Pruebas de validación de URLs por entorno

**Título sugerido:** `Pruebas: validar reglas de configuración de URLs de entorno`

**Tipo:** Pruebas / Configuración

**Descripción:**
La configuración de entornos debe ser predecible y segura, evitando URLs inválidas en operación.

**Tareas:**
- Crear pruebas unitarias para validación de esquema y host.
- Cubrir casos válidos e inválidos por entorno.
- Verificar persistencia de entorno activo y URL efectiva.
- Agregar casos de regresión para cambios futuros.

**Criterios de aceptación:**
- Validación robusta de URLs con cobertura de casos límite.
- No se permite guardar configuración inválida.
- La URL activa coincide con el entorno seleccionado.

**Estimación:** 3 puntos

---

## Incidencia 3: Pruebas de mapeo de estados de envío SMS

**Título sugerido:** `Pruebas: cubrir mapeo de resultados de envío SMS a estados de negocio`

**Tipo:** Pruebas / Mensajería

**Descripción:**
El mapeo de resultados del sistema Android a estados funcionales debe estar testeado para asegurar reportes correctos al backend.

**Tareas:**
- Probar mapeo de códigos de resultado a `ENVIADO` y `FALLIDO`.
- Cubrir estados desconocidos y errores no contemplados.
- Validar actualización de estado enviada al repositorio.
- Documentar matriz de mapeo y comportamiento esperado.

**Criterios de aceptación:**
- Cobertura de todos los códigos utilizados en producción.
- Mapeo consistente y estable ante cambios.
- Sin inconsistencias entre resultado local y estado reportado.

**Estimación:** 5 puntos

---

## Incidencia 4: Pruebas de reintentos y comportamiento de backoff

**Título sugerido:** `Pruebas: verificar política de reintentos y backoff del flujo SMS`

**Tipo:** Pruebas / Confiabilidad

**Descripción:**
La política de reintentos definida en Sprint 2 debe verificarse con pruebas para asegurar comportamiento estable bajo fallas repetidas.

**Tareas:**
- Implementar pruebas de tiempos de reintento y límites máximos.
- Simular secuencias de error recuperable y no recuperable.
- Verificar transición de estados ante recuperación de red.
- Generar evidencia de comportamiento esperado.

**Criterios de aceptación:**
- Backoff aplicado según política definida.
- No hay bucles agresivos de reintento.
- El flujo se recupera cuando la causa del error desaparece.

**Estimación:** 5 puntos

---

## Incidencia 5: Limpieza de dependencias y configuración de compilación

**Título sugerido:** `Calidad: eliminar duplicidades de dependencias y sanear Gradle`

**Tipo:** Calidad / Deuda técnica

**Descripción:**
Se requiere depurar dependencias repetidas y mejorar claridad de configuración para facilitar mantenimiento del build.

**Tareas:**
- Eliminar dependencias duplicadas en `build.gradle.kts`.
- Revisar versiones y consistencia de bibliotecas principales.
- Verificar compilación y pruebas después de la limpieza.
- Registrar decisiones en bitácora técnica.

**Criterios de aceptación:**
- Archivo de build sin duplicidades obvias.
- Compilación exitosa con configuración limpia.
- Menor complejidad de mantenimiento del proyecto.

**Estimación:** 3 puntos

---

## Incidencia 6: Documentación operativa por entorno y certificados

**Título sugerido:** `Documentación: definir operación por entorno y gestión de certificados`

**Tipo:** Documentación / Operación

**Descripción:**
La operación del agente requiere documentación clara para despliegue, soporte y rotación de certificados.

**Tareas:**
- Documentar flujo de despliegue para DEV, PREPROD y PROD.
- Definir proceso de actualización y rotación de certificados.
- Incluir checklist de verificación posterior al despliegue.
- Publicar guía de respuesta ante incidentes de conectividad TLS.

**Criterios de aceptación:**
- Guía operativa completa y versionada en repositorio.
- Procedimiento de certificados aplicable y verificable.
- Equipo técnico puede ejecutar despliegue sin dependencias implícitas.

**Estimación:** 3 puntos

---

## Definición de terminado del sprint

- Todas las incidencias cerradas con solicitud de extracción vinculada.
- Suite de pruebas de dominio integrada y ejecutándose en flujo de integración continua.
- Configuración de compilación saneada y documentada.
- Documentación operativa aprobada por responsables técnicos.

## Dependencias sugeridas

1. Incidencia 1
2. Incidencia 2
3. Incidencia 3
4. Incidencia 4
5. Incidencia 5
6. Incidencia 6

## Comandos sugeridos (si se usa GitHub CLI)

> Requiere repositorio con remoto configurado y autenticación previa con `gh auth login`.

- Crear hito:
  - `gh api repos/<owner>/<repo>/milestones -f title='Sprint 3 - Calidad continua y documentación' -f state='open'`
- Crear incidencia:
  - `gh issue create --title '<titulo>' --body-file <archivo.md> --label sprint-3 --label prioridad:media`

