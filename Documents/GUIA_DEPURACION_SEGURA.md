# Guía de depuración segura

## Objetivo
Definir lineamientos de observabilidad para diagnóstico técnico sin exponer credenciales ni datos sensibles.

## Política de registros HTTP

### Compilación de depuración (`debug`)
- Se habilita `HttpLoggingInterceptor` en nivel `BASIC`.
- Este nivel registra información mínima de trazabilidad (método, URL, código de respuesta y tiempos), sin cuerpos completos.
- Se aplica `redactHeader("Authorization")` para ocultar cabeceras sensibles.

### Compilación de producción (`release`)
- Se establece `HttpLoggingInterceptor.Level.NONE`.
- No se registran cuerpos de solicitud ni de respuesta.

## Manejo de secretos
- No registrar tokens en logs de éxito o error.
- No incluir credenciales ni datos personales en mensajes de diagnóstico.
- Si ocurre un error en renovación de sesión, registrar un mensaje técnico genérico y, de ser necesario, la excepción para análisis interno sin concatenar secretos.

## Trazabilidad técnica conservada
- Se mantiene capacidad de diagnóstico por:
  - Código HTTP de respuesta.
  - Resultado de renovación de sesión (éxito/fallo).
  - Errores controlados en cliente de red.
