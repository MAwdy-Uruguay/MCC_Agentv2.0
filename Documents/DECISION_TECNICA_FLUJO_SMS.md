# Decisión técnica: flujo único de envío SMS con WorkManager

## Contexto
La base de código tenía coexistencia histórica entre un servicio en primer plano y tareas con WorkManager, lo que incrementaba el riesgo de doble programación y competencia por el mismo lote de mensajes pendientes.

## Decisión
Se adopta **WorkManager** como único mecanismo activo para sincronización y envío de SMS.

## Justificación técnica
- Permite planificación persistente y compatible con reinicios del dispositivo.
- Ofrece política de unicidad con `enqueueUniquePeriodicWork` para evitar doble programación.
- Reduce complejidad operativa al eliminar rutas de ejecución paralelas.

## Implementación aplicada
1. Se elimina el servicio legado de SMS y su declaración en `AndroidManifest.xml`.
2. Se configura un trabajo periódico único (`sms_sync_periodico`) con `ExistingPeriodicWorkPolicy.KEEP`.
3. Se mantiene una bandera de estado (`sms_service_running`) para decidir, tras reinicio, si se reprograma el flujo.
4. En cada ciclo, el worker marca el mensaje como `ENVIANDO` antes del despacho para disminuir reenvíos por reintentos.
5. Se usa `requestCode` por `mid` en `PendingIntent` para aislar confirmaciones por mensaje.

## Garantías frente a duplicados
- No hay competencia entre worker y servicio porque el servicio fue retirado.
- No hay doble alta del worker por uso de trabajo periódico único con política `KEEP`.
- El marcado previo `ENVIANDO` y la correlación por `mid` reducen reenvíos accidentales en escenarios de retry.

## Comportamiento tras reinicio
- `BootReceiver` reprograma el flujo solo si la bandera de ejecución activa está habilitada.
- Con esto se conserva operación tras reinicio sin activar envíos cuando el flujo fue detenido por cierre de sesión.
