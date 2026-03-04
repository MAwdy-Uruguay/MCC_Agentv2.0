# Sprint 2 — Lista de incidencias para GitHub

Este sprint implementa la **Fase 2** de la hoja de ruta: consolidación de arquitectura de ejecución SMS y fortalecimiento de confiabilidad operativa.

## Objetivo del sprint

Simplificar la arquitectura de envío, mejorar robustez ante fallos y asegurar trazabilidad técnica en la sincronización de mensajes SMS.

## Duración sugerida

- 2 semanas

## Estructura recomendada en GitHub

- Hito: `Sprint 2 - Consolidación operativa SMS`
- Etiquetas sugeridas:
  - `sprint-2`
  - `arquitectura`
  - `mensajeria`
  - `confiabilidad`
  - `android`
  - `prioridad:alta`

---

## Incidencia 1: Implementar arquitectura única de envío SMS

**Título sugerido:** `Arquitectura: activar un único flujo de envío SMS en producción`

**Tipo:** Arquitectura / Operación

**Descripción:**
Luego del saneamiento inicial, se debe cerrar la transición y operar con una única ruta de ejecución de envíos para evitar duplicidad y estados inconsistentes.

**Tareas:**
- Consolidar definitivamente la estrategia aprobada (WorkManager o Servicio en primer plano).
- Eliminar invocaciones remanentes de la estrategia descartada.
- Revisar inicialización desde `MainActivity` y arranque posterior a reinicio.
- Documentar decisión técnica y consecuencias de diseño.

**Criterios de aceptación:**
- Existe un solo mecanismo activo de envío.
- No se detectan ejecuciones simultáneas de dos rutas.
- La aplicación reanuda el flujo correctamente tras reinicio.

**Estimación:** 8 puntos

---

## Incidencia 2: Estandarizar política de reintentos y backoff

**Título sugerido:** `Confiabilidad: definir política unificada de reintentos y backoff`

**Tipo:** Robustez / Operación

**Descripción:**
Los reintentos y tiempos de espera deben seguir una política clara para evitar saturación, retardos excesivos y comportamientos impredecibles.

**Tareas:**
- Definir tabla de tiempos de reintento por tipo de error.
- Unificar lógica de espera y recuperación en el flujo activo.
- Diferenciar errores recuperables de no recuperables.
- Agregar métricas mínimas en registros para auditoría operativa.

**Criterios de aceptación:**
- Existe política documentada de reintentos.
- Los reintentos siguen comportamiento consistente ante fallos.
- Se evita ciclo agresivo de reenvío continuo.

**Estimación:** 5 puntos

---

## Incidencia 3: Mejorar correlación de mensajes y confirmaciones

**Título sugerido:** `Mensajería: robustecer correlación entre envío SMS y estado reportado`

**Tipo:** Mensajería / Integración

**Descripción:**
Es necesario reforzar la correlación entre cada envío y su actualización de estado para prevenir reportes incorrectos en backend.

**Tareas:**
- Validar `requestCode` único por mensaje en toda la ruta activa.
- Estandarizar metadatos mínimos por envío (`mid`, timestamp, resultado).
- Revisar manejo de callbacks de envío exitoso/fallido.
- Asegurar idempotencia básica en actualización de estado.

**Criterios de aceptación:**
- Cada confirmación corresponde al `mid` correcto.
- No hay colisiones de intents entre mensajes concurrentes.
- No se generan actualizaciones duplicadas por el mismo evento.

**Estimación:** 5 puntos

---

## Incidencia 4: Endurecer gestión de errores de red en repositorios

**Título sugerido:** `Integración: normalizar manejo de errores de red y respuestas API`

**Tipo:** Integración / Calidad

**Descripción:**
El manejo de errores debe ser homogéneo para mejorar diagnóstico y evitar silencios operativos cuando falla el backend.

**Tareas:**
- Definir modelo común de error para repositorios de mensajes/autenticación.
- Incorporar clasificación de errores HTTP y excepciones de transporte.
- Mejorar mensajes de diagnóstico sin exponer datos sensibles.
- Validar que fallos temporales no rompen el ciclo de sincronización.

**Criterios de aceptación:**
- Los errores quedan clasificados y trazables.
- El flujo continúa en condiciones recuperables.
- Los registros no exponen secretos.

**Estimación:** 3 puntos

---

## Incidencia 5: Revisar navegación y estado de sesión al inicio

**Título sugerido:** `Experiencia: definir destino inicial según estado de sesión`

**Tipo:** Experiencia / Arquitectura

**Descripción:**
La navegación inicial debe responder al estado de autenticación para reducir fricción de uso y comportamientos ambiguos.

**Tareas:**
- Evaluar token/sesión al iniciar la app.
- Definir destino inicial dinámico en navegación.
- Evitar estados intermedios inconsistentes entre login y home.
- Validar comportamiento en login, logout y token vencido.

**Criterios de aceptación:**
- El destino inicial refleja el estado real de sesión.
- El usuario autenticado no regresa innecesariamente a login.
- El token vencido redirige correctamente al flujo de autenticación.

**Estimación:** 3 puntos

---

## Incidencia 6: Cierre de deuda técnica de la capa de red

**Título sugerido:** `Deuda técnica: eliminar duplicidades y simplificar contrato de API`

**Tipo:** Deuda técnica

**Descripción:**
Se requiere reducir duplicidades pendientes para facilitar mantenimiento y evolución.

**Tareas:**
- Unificar endpoint de renovación de token en la interfaz API.
- Eliminar rutas o métodos redundantes no utilizados.
- Revisar nombres y coherencia de modelos de datos.
- Actualizar bitácora técnica con decisiones de simplificación.

**Criterios de aceptación:**
- Sin métodos duplicados de renovación en el contrato API.
- Menor complejidad en flujo de autenticación y red.
- Código más legible y consistente.

**Estimación:** 3 puntos

---

## Definición de terminado del sprint

- Todas las incidencias cerradas con solicitud de extracción vinculada.
- Validación funcional completa del flujo SMS de extremo a extremo.
- Bitácora de decisiones arquitectónicas actualizada.
- Evidencias de pruebas manuales o automatizadas anexadas al hito.

## Dependencias sugeridas

1. Incidencia 1
2. Incidencia 2
3. Incidencia 3
4. Incidencia 4
5. Incidencia 6
6. Incidencia 5

## Comandos sugeridos (si se usa GitHub CLI)

> Requiere repositorio con remoto configurado y autenticación previa con `gh auth login`.

- Crear hito:
  - `gh api repos/<owner>/<repo>/milestones -f title='Sprint 2 - Consolidación operativa SMS' -f state='open'`
- Crear incidencia:
  - `gh issue create --title '<titulo>' --body-file <archivo.md> --label sprint-2 --label prioridad:alta`

