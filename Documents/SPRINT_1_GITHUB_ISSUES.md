# Sprint 1 — Lista de incidencias para GitHub

Este sprint implementa la **Fase 1** definida en la hoja de ruta: endurecimiento de seguridad y limpieza inicial para estabilizar la operación.

## Objetivo del sprint

Reducir riesgos críticos de seguridad y deuda técnica operativa con cambios de bajo impacto funcional y alto impacto en confiabilidad.

## Duración sugerida

- 2 semanas

## Estructura recomendada en GitHub

- Hito: `Sprint 1 - Seguridad y saneamiento`
- Etiquetas sugeridas:
  - `sprint-1`
  - `seguridad`
  - `arquitectura`
  - `android`
  - `deuda-tecnica`
  - `prioridad:alta`

---

## Incidencia 1: Endurecer política de tráfico seguro por entorno

**Título sugerido:** `Seguridad: restringir tráfico en texto plano y aplicar configuración de seguridad de red por entorno`

**Tipo:** Seguridad / Configuración

**Descripción:**
Actualmente la aplicación permite tráfico no cifrado (`usesCleartextTraffic=true`). Se debe restringir el tráfico HTTP en producción y mantener excepciones controladas solo en entornos internos cuando corresponda.

**Tareas:**
- Definir política por sabor y tipo de compilación para DEV, PREPROD y PROD.
- Ajustar `AndroidManifest.xml` para no permitir tráfico en texto plano de forma global.
- Configurar `network_security_config` específico por entorno.
- Validar conectividad HTTPS en cada entorno soportado.

**Criterios de aceptación:**
- En producción no se permite tráfico HTTP plano.
- Cualquier excepción de texto plano queda limitada y documentada por entorno.
- La aplicación mantiene conectividad en endpoints válidos HTTPS.

**Estimación:** 5 puntos

---

## Incidencia 2: Limitar registro HTTP a depuración y ocultar datos sensibles

**Título sugerido:** `Seguridad: restringir registros HTTP a depuración y sanitizar credenciales`

**Tipo:** Seguridad / Observabilidad

**Descripción:**
El registro en nivel BODY puede exponer tokens y datos sensibles. Debe quedar habilitado solo para depuración y con sanitización mínima de cabeceras sensibles.

**Tareas:**
- Condicionar `HttpLoggingInterceptor` por `BuildConfig.DEBUG`.
- Evitar impresión de `Authorization` y otros secretos.
- Revisar registros de errores para no incluir tokens ni datos personales.
- Actualizar guía de depuración segura.

**Criterios de aceptación:**
- En compilación de producción no se registran cuerpos de solicitud/respuesta.
- No se registran tokens ni cabeceras sensibles en ninguna compilación.
- Se conserva trazabilidad suficiente para diagnóstico técnico.

**Estimación:** 3 puntos

---

## Incidencia 3: Migrar token a almacenamiento cifrado

**Título sugerido:** `Seguridad: migrar sesión a almacenamiento cifrado`

**Tipo:** Seguridad / Persistencia

**Descripción:**
El token de autenticación se guarda en `SharedPreferences` sin cifrado. Debe migrarse a almacenamiento seguro.

**Tareas:**
- Integrar almacenamiento cifrado (`EncryptedSharedPreferences` o equivalente).
- Implementar migración no disruptiva de token existente.
- Ajustar lectura/escritura de sesión en componentes afectados.
- Verificar comportamiento de inicio de sesión, renovación y cierre de sesión.

**Criterios de aceptación:**
- El token no se guarda en texto plano.
- La sesión previa se migra correctamente sin romper el inicio de sesión.
- El cierre de sesión elimina credenciales de forma segura.

**Estimación:** 5 puntos

---

## Incidencia 4: Unificar mecanismo de ejecución SMS

**Título sugerido:** `Arquitectura: consolidar envío SMS en un único mecanismo de ejecución`

**Tipo:** Arquitectura / Operación

**Descripción:**
Existen dos estrategias (`SMSService` y `SmsSyncWorker`) con riesgo de duplicidad y mayor complejidad. Debe elegirse una única estrategia de ejecución.

**Tareas:**
- Definir decisión técnica (preferencia sugerida: WorkManager para sincronización periódica controlada).
- Eliminar rutas no usadas y código muerto/comentado.
- Ajustar reprogramación en reinicio al mecanismo elegido.
- Validar ausencia de envíos duplicados en escenarios de reintento.

**Criterios de aceptación:**
- Solo existe un flujo activo de envío de SMS.
- No hay doble programación ni competencia entre worker/servicio.
- El flujo sigue operativo tras reinicio del dispositivo.

**Estimación:** 8 puntos

---

## Incidencia 5: Corregir colisiones de PendingIntent en envíos SMS

**Título sugerido:** `Confiabilidad: evitar colisiones de PendingIntent por mensaje`

**Tipo:** Robustez / Mensajería

**Descripción:**
El uso de `requestCode` fijo puede provocar reutilización no deseada de intents y estados incorrectos por mensaje.

**Tareas:**
- Usar `requestCode` único por `mid` (por ejemplo `mid.hashCode()`).
- Revisar coherencia en `SmsSyncWorker` y en la ruta de envío adoptada.
- Probar múltiples mensajes en cola con resultados mixtos (éxito/fallo).

**Criterios de aceptación:**
- Cada SMS mantiene correlación correcta con su confirmación de estado.
- No se observan colisiones en pruebas con lotes de mensajes.

**Estimación:** 3 puntos

---

## Incidencia 6: Limpieza inicial de deuda técnica visible

**Título sugerido:** `Deuda técnica: eliminar código comentado legado y duplicidades inmediatas`

**Tipo:** Deuda técnica

**Descripción:**
Existe código comentado extenso y duplicidades que dificultan mantenimiento y revisión.

**Tareas:**
- Eliminar bloques comentados obsoletos.
- Remover duplicidades obvias en contrato API (renovación de token duplicada).
- Registrar decisiones en bitácora técnica del sprint.

**Criterios de aceptación:**
- Código fuente sin bloques comentados de implementaciones viejas.
- Contrato de API sin duplicidades funcionales innecesarias.
- Revisión de pares aprobada por legibilidad y mantenibilidad.

**Estimación:** 3 puntos

---

## Definición de terminado del sprint

- Todas las incidencias cerradas con solicitud de extracción vinculada.
- Validación técnica en entorno de pruebas.
- Lista de verificación de seguridad actualizada.
- Documento de decisiones arquitectónicas del sprint publicado.

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
  - `gh api repos/<owner>/<repo>/milestones -f title='Sprint 1 - Seguridad y saneamiento' -f state='open'`
- Crear incidencia:
  - `gh issue create --title '<titulo>' --body-file <archivo.md> --label sprint-1 --label prioridad:alta`

