# Revisión técnica del proyecto MCC Agent

## 1) Objetivo principal identificado

El proyecto **MCC Agent** es una aplicación Android cuyo objetivo principal es actuar como un **agente intermedio entre un backend y la telefonía del dispositivo**, permitiendo:

- autenticación de usuario contra API,
- obtención de mensajes pendientes desde servidor,
- envío de SMS desde el teléfono,
- y retroalimentación del estado de entrega al backend.

Este comportamiento sugiere un caso de uso operativo (pasarela/bridge SMS) donde el dispositivo ejecuta tareas de sincronización periódicas y persistentes incluso tras reinicios.

## 2) Funcionalidades actuales del sistema

### 2.1 Autenticación y sesión

- Pantalla de login con usuario/contraseña y gestión de token.
- Renovación de token implementada en cliente de red.
- Cierre de sesión mediante `SessionManager`.

### 2.2 Configuración de entornos

- Pantalla de configuración para URLs de **DEV/PREPROD/PROD**.
- Selección de entorno activo y persistencia en `SharedPreferences`.
- Validación básica de formato URL y prueba de conectividad.

### 2.3 Integración con API

- Cliente Retrofit con interceptores de autorización.
- Carga de certificado según entorno para TLS custom.
- Endpoints para login, cliente, dispositivos, mensajes y actualización de estado.

### 2.4 Orquestación de envío SMS

- Scheduler con WorkManager (`SmsWorkScheduler`) para sincronización diferida.
- Worker (`SmsSyncWorker`) que consulta mensajes pendientes y dispara envío por `SmsManager`.
- Receiver (`SmsSentReceiver`) que recibe resultado del envío y notifica estado al backend.
- Rearme automático al reiniciar dispositivo vía `BootReceiver`.

### 2.5 Interfaz y navegación

- UI en Jetpack Compose.
- Navegación entre login, home y settings.

## 3) Hallazgos técnicos y defectos potenciales

## 3.1 Seguridad

1. **`usesCleartextTraffic=true` en manifiesto**.
   - Permite tráfico HTTP no cifrado si se utiliza accidentalmente una URL insegura.

2. **Logging de red en nivel BODY sin condicionarlo por build type**.
   - Riesgo de exponer tokens, payloads sensibles y datos de operación en logs.

3. **Token en `SharedPreferences` sin cifrado**.
   - Riesgo en dispositivos comprometidos o con respaldos inseguros.

4. **Permisos amplios de SMS siempre declarados**.
   - Correcto para el caso de uso, pero requiere endurecer justificación y gobernanza de uso para publicación/distribución.

## 3.2 Robustez y arquitectura

1. **Redundancia en la capa de envío SMS**.
   - Conviven `SMSService` (foreground loop) y `SmsSyncWorker` (WorkManager), con indicios de uso mixto y código legado comentado.
   - Esto incrementa complejidad operativa y riesgo de duplicidad de envíos.

2. **Código comentado extenso en producción**.
   - Dificulta mantenimiento y revisión de comportamiento real.

3. **Colisión potencial de `PendingIntent`**.
   - En worker y servicio se usa requestCode fijo (`0`), lo que puede provocar reutilización no deseada entre mensajes.

4. **Navegación inicial fija en login**.
   - No se observa evaluación explícita del estado de sesión para definir destino inicial.

5. **Inconsistencia menor en `SettingsScreen`**.
   - Se recibe `context` por parámetro pero se vuelve a declarar localmente con `LocalContext.current`.

6. **Interfaz API con duplicidad semántica de renovación**.
   - Existen `renew()` y `renewToken()` apuntando al mismo endpoint.

## 3.3 Calidad y deuda técnica

1. **Dependencias duplicadas en Gradle** (ej. Compose/UI/Core declaradas varias veces).
2. **Pruebas unitarias/instrumentadas de plantilla** sin cobertura funcional relevante del dominio.
3. **Valores por defecto de URL potencialmente no productivos** (ej. host de desarrollo local).

## 4) Mejoras recomendadas (priorizadas)

## Prioridad alta (seguridad y operación)

1. **Eliminar `usesCleartextTraffic=true` en producción** y limitar HTTP únicamente a entornos internos controlados mediante `network_security_config` por flavor.
2. **Restringir logging HTTP a builds debug** y sanitizar cabeceras sensibles.
3. **Migrar token a almacenamiento cifrado** (p. ej. `EncryptedSharedPreferences` o Jetpack Security).
4. **Unificar estrategia de ejecución**: elegir WorkManager *o* Foreground Service como camino único para envío de SMS.
5. **Corregir requestCode de `PendingIntent`** usando `mid.hashCode()` para evitar colisiones.

## Prioridad media (mantenibilidad)

1. **Eliminar código comentado legado** y conservar historial en Git.
2. **Refactorizar `SettingsScreen`** para evitar sombra de variables y simplificar estado.
3. **Consolidar contrato de API** eliminando duplicidades (`renew` vs `renewToken`).
4. **Revisar `startDestination`** con lógica de sesión persistida.

## Prioridad baja (calidad continua)

1. **Limpieza de dependencias duplicadas** en `build.gradle.kts`.
2. **Agregar pruebas de dominio**:
   - parsing/expiración de JWT,
   - validación de URL de entornos,
   - mapeo de estados de envío SMS,
   - comportamiento de reintentos/backoff.
3. **Definir documentación operativa** para despliegue por entorno y rotación de certificados.

## 5) Propuesta de hoja de ruta breve

- **Fase 1 (rápida):** endurecimiento de seguridad (cleartext/logs/token) y limpieza de código comentado.
- **Fase 2:** consolidación de arquitectura de ejecución SMS y robustez de intents/reintentos.
- **Fase 3:** pruebas automatizadas de negocio + mejora de DX (build limpio, documentación técnica).

## 6) Conclusión

El proyecto tiene una base funcional sólida para su propósito de agente SMS con backend, pero presenta riesgos típicos de una solución que evolucionó rápido (seguridad de transporte/credenciales, doble mecanismo de ejecución y deuda técnica). Atacar primero seguridad y simplificación operativa ofrecerá la mayor reducción de riesgo con impacto directo en estabilidad y mantenibilidad.
