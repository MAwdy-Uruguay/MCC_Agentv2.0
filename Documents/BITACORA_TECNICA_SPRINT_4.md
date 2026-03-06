# Bitácora técnica del sprint 4

## Resumen ejecutivo
Durante este sprint se priorizó la mantenibilidad del módulo Android, enfocándose en limpieza de código legado, simplificación del contrato de API y consistencia documental de decisiones técnicas ya implementadas.

## Decisiones registradas

### 1) Eliminación de bloques comentados obsoletos
- Se retiraron bloques comentados heredados en archivos de tema tipográfico y estado de cliente.
- Criterio aplicado: cualquier implementación antigua debe permanecer en historial Git, no en código activo.

### 2) Contrato de API sin duplicidad de renovación
- Se eliminó la operación duplicada `renew()` en el contrato `IApiService`.
- Se mantuvo únicamente `renewToken()` para el endpoint `auth/renew`, alineado con el flujo vigente de `RetrofitClient`.
- Se ajustó `IAuthRepository` y `AuthRepositoryImpl` para retirar el método de renovación no utilizado.

### 3) Legibilidad y mantenibilidad
- Se normalizó formato de archivos tocados y se redujo ruido visual de plantillas no utilizadas.
- Se deja trazabilidad de esta decisión en la presente bitácora para revisión de pares.

## Impacto esperado
- Menor ambigüedad para nuevas incorporaciones al equipo.
- Menos riesgo de regresiones por uso accidental de contratos duplicados.
- Código más directo para revisión y auditoría técnica.

## Validación realizada
- Verificación estática de ausencia de la firma `renew()` en contratos/repositorio.
- Revisión estática de bloques comentados removidos en los archivos intervenidos.
