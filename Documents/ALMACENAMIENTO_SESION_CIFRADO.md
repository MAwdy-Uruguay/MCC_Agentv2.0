# Almacenamiento cifrado de sesión y migración de token

## Objetivo
Asegurar que el token de autenticación no se persista en texto plano y que los usuarios con sesión previa puedan continuar operando sin interrupciones tras la migración.

## Implementación

### 1) Almacenamiento cifrado
Se incorporó `EncryptedSharedPreferences` con `MasterKey` para guardar el token en un archivo de preferencias dedicado:
- Archivo seguro: `mcc_secure_prefs`
- Clave de token: `token`

### 2) Migración no disruptiva
Se implementó una migración automática desde el almacenamiento legado (`mcc_prefs`) hacia el almacenamiento cifrado:
1. Se intenta leer token desde `mcc_secure_prefs`.
2. Si no existe, se busca en `mcc_prefs`.
3. Si existe en legado, se copia a `mcc_secure_prefs` y se elimina de `mcc_prefs`.

La migración se dispara al iniciar la app y también cuando cualquier componente solicita el token.

### 3) Componentes ajustados
- Inicio de sesión: guarda token únicamente en almacenamiento cifrado.
- Cliente de red/renovación: lee y actualiza token en almacenamiento cifrado.
- Cierre de sesión: elimina token tanto del almacenamiento cifrado como del legado para asegurar limpieza completa.
- Navegación inicial: decide pantalla de inicio (`home`/`login`) según la disponibilidad del token migrado/cifrado.

## Criterios de aceptación cubiertos
- **El token no se guarda en texto plano:** cumplido mediante `EncryptedSharedPreferences`.
- **La sesión previa se migra correctamente:** cumplido con migración automática y transparente.
- **El cierre de sesión elimina credenciales de forma segura:** cumplido al remover token de almacenamiento seguro y legado.
