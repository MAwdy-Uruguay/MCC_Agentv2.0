# Prueba manual de envío SMS a +59895024444

## Objetivo
Validar que los mensajes en estado `PENDIENTE` sean realmente despachados por el `SmsSyncWorker`.

## Precondiciones
- Dispositivo Android físico con SIM activa y saldo/habilitación para SMS.
- Permiso `SEND_SMS` concedido a la app.
- Sesión autenticada contra el backend.
- Existencia de al menos un mensaje `PENDIENTE` cuyo destinatario sea `+59895024444`.

## Cambios aplicados para facilitar la validación
- Se mantiene trabajo periódico cada 15 minutos.
- Además, al iniciar el flujo se dispara una sincronización inmediata (`OneTimeWorkRequest`) para no esperar la ventana periódica.

## Pasos
1. Abrir la aplicación y conceder todos los permisos solicitados.
2. Confirmar en logs que se agenda el flujo de sincronización.
3. Insertar/confirmar en backend un mensaje `PENDIENTE` para `+59895024444`.
4. Esperar la ejecución inmediata del worker.
5. Verificar en logcat:
   - `Mensajes pendientes recibidos: N`
   - `SMS despachado para confirmación de entrega. mid=... destino=+59895024444`
6. Verificar en backend que el estado avance de `PENDIENTE` a `ENVIANDO` y luego a `ENVIADO` o `FALLIDO`.

## Comandos útiles (adb)
```bash
adb logcat | grep -E "SmsSyncWorker|SmsSentReceiver|MessageRepo"
```

```bash
adb shell dumpsys activity service WorkManager
```

## Resultado esperado
- El mensaje `PENDIENTE` dirigido a `+59895024444` debe despacharse sin esperar 15 minutos.
- Debe existir traza completa de ciclo: lectura de pendientes, intento de envío y confirmación de estado.
