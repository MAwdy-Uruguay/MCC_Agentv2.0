---
id: sprint-1-seguridad-saneamiento
title: Sprint 1 - Seguridad y saneamiento
mode: apply
---

```yaml
milestone:
  title: "Sprint 1 - Seguridad y saneamiento"
  description: "Endurecimiento de seguridad y limpieza inicial para estabilizar la operación con bajo impacto funcional y alto impacto en confiabilidad."
  due_on: "2026-03-20"

labels:
  - sprint-1

ops:
  - op: create_issue
    title: "Seguridad: restringir tráfico en texto plano y aplicar configuración de seguridad de red por entorno"
    type: "Task"
    labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
    estimate: 5
    body: |
      Definir política por sabor y tipo de compilación para DEV, PREPROD y PROD.
      Ajustar AndroidManifest.xml para no permitir tráfico en texto plano de forma global.
      Configurar network_security_config específico por entorno.
      Validar conectividad HTTPS en cada entorno soportado.

      Criterios de aceptación:
      - En producción no se permite tráfico HTTP plano.
      - Cualquier excepción de texto plano queda limitada y documentada por entorno.
      - La aplicación mantiene conectividad en endpoints válidos HTTPS.
  - op: create_issue
    title: "Seguridad: restringir registros HTTP a depuración y sanitizar credenciales"
    type: "Task"
    labels: ["sprint-1", "seguridad", "arquitectura", "prioridad:alta"]
    estimate: 3
    body: |
      Condicionar HttpLoggingInterceptor por BuildConfig.DEBUG.
      Evitar impresión de Authorization y otros secretos.
      Revisar registros de errores para no incluir tokens ni datos personales.
      Actualizar guía de depuración segura.

      Criterios de aceptación:
      - En compilación de producción no se registran cuerpos de solicitud/respuesta.
      - No se registran tokens ni cabeceras sensibles en ninguna compilación.
      - Se conserva trazabilidad suficiente para diagnóstico técnico.
  - op: create_issue
    title: "Seguridad: migrar sesión a almacenamiento cifrado"
    type: "Feature"
    labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
    estimate: 5
    body: |
      Integrar almacenamiento cifrado (EncryptedSharedPreferences o equivalente).
      Implementar migración no disruptiva de token existente.
      Ajustar lectura/escritura de sesión en componentes afectados.
      Verificar comportamiento de inicio de sesión, renovación y cierre de sesión.

      Criterios de aceptación:
      - El token no se guarda en texto plano.
      - La sesión previa se migra correctamente sin romper el inicio de sesión.
      - El cierre de sesión elimina credenciales de forma segura.
  - op: create_issue
    title: "Arquitectura: consolidar envío SMS en un único mecanismo de ejecución"
    type: "Task"
    labels: ["sprint-1", "arquitectura", "android", "prioridad:alta"]
    estimate: 8
    body: |
      Definir decisión técnica (preferencia sugerida: WorkManager para sincronización periódica controlada).
      Eliminar rutas no usadas y código muerto/comentado.
      Ajustar reprogramación en reinicio al mecanismo elegido.
      Validar ausencia de envíos duplicados en escenarios de reintento.

      Criterios de aceptación:
      - Solo existe un flujo activo de envío de SMS.
      - No hay doble programación ni competencia entre worker/servicio.
      - El flujo sigue operativo tras reinicio del dispositivo.
  - op: create_issue
    title: "Confiabilidad: evitar colisiones de PendingIntent por mensaje"
    type: "Bug"
    labels: ["sprint-1", "android", "prioridad:alta"]
    estimate: 3
    body: |
      Usar requestCode único por mid (por ejemplo mid.hashCode()).
      Revisar coherencia en SmsSyncWorker y en la ruta de envío adoptada.
      Probar múltiples mensajes en cola con resultados mixtos (éxito/fallo).

      Criterios de aceptación:
      - Cada SMS mantiene correlación correcta con su confirmación de estado.
      - No se observan colisiones en pruebas con lotes de mensajes.
  - op: create_issue
    title: "Deuda técnica: eliminar código comentado legado y duplicidades inmediatas"
    type: "Task"
    labels: ["sprint-1", "deuda-tecnica", "prioridad:alta"]
    estimate: 3
    body: |
      Eliminar bloques comentados obsoletos.
      Remover duplicidades obvias en contrato API (renovación de token duplicada).
      Registrar decisiones en bitácora técnica del sprint.

      Criterios de aceptación:
      - Código fuente sin bloques comentados de implementaciones viejas.
      - Contrato de API sin duplicidades funcionales innecesarias.
      - Revisión de pares aprobada por legibilidad y mantenibilidad.
```
