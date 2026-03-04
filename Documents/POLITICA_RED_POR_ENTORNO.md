# Política de red por entorno (DEV, PREPROD, PROD)

## Objetivo
Definir y documentar las reglas de tráfico de red por sabor y tipo de compilación, asegurando que el tráfico en texto plano (HTTP) no quede habilitado globalmente y que solo exista una excepción controlada para DEV.

## Sabor y tipo de compilación
Se incorporaron los sabores:
- `dev`
- `preprod`
- `prod`

Y se mantienen los tipos de compilación:
- `debug`
- `release`

Además, se exponen campos de compilación para trazabilidad:
- `BuildConfig.ENTORNO`
- `BuildConfig.TIPO_COMPILACION`

## Reglas de seguridad aplicadas

### Regla global (todas las variantes)
- En `AndroidManifest.xml` principal:
  - `android:usesCleartextTraffic="false"`
  - `android:networkSecurityConfig="@xml/network_security_config"`
- En configuración base de red: `cleartextTrafficPermitted="false"`.

### Excepción limitada a DEV
- En `src/dev/AndroidManifest.xml` se reemplaza la política a `android:usesCleartextTraffic="true"`.
- En `src/dev/res/xml/network_security_config.xml` se mantienen excepciones de texto plano solo para hosts internos de desarrollo:
  - `10.160.67.14`
  - `suy002001-dev.int.mapfre.net`

### PREPROD y PROD
- En `preprod` y `prod` el tráfico en texto plano permanece deshabilitado.
- No se agregaron excepciones HTTP en `prod`.

## Validación ejecutada
1. Compilación de variantes representativas:
   - `devDebug`
   - `preprodDebug`
   - `prodRelease`
2. Pruebas unitarias:
   - Verificación de que las URLs base por defecto de DEV, PREPROD y PROD usan esquema `https://`.

## Resultado esperado
- Producción no permite tráfico HTTP plano.
- Cualquier excepción de texto plano está explícitamente acotada y documentada en DEV.
- Se mantiene la conectividad sobre endpoints HTTPS válidos por configuración.
