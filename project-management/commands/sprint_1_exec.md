```yaml
milestone: "Sprint 1 - Seguridad y saneamiento"
labels:
  - sprint-1
  - prioridad:alta
ops:
  - op: create_issue
    create_issue:
      title: "Definir política por sabor y tipo de compilación para DEV, PREPROD y PROD"
      type: Security
      estimate: 1
      body: |
        Definir política por sabor y tipo de compilación para DEV, PREPROD y PROD.

  - op: create_issue
    create_issue:
      title: "Ajustar AndroidManifest.xml para no permitir tráfico en texto plano de forma global"
      type: Security
      estimate: 2
      body: |
        Ajustar AndroidManifest.xml para no permitir tráfico en texto plano de forma global.

  - op: create_issue
    create_issue:
      title: "Configurar network_security_config específico por entorno"
      type: Security
      estimate: 1
      body: |
        Configurar network_security_config específico por entorno.

  - op: create_issue
    create_issue:
      title: "Validar conectividad HTTPS en cada entorno soportado"
      type: Test
      estimate: 1
      body: |
        Validar conectividad HTTPS en cada entorno soportado.

  - op: create_issue
    create_issue:
      title: "Condicionar HttpLoggingInterceptor por BuildConfig.DEBUG"
      type: Security
      estimate: 1
      body: |
        Condicionar HttpLoggingInterceptor por BuildConfig.DEBUG.

  - op: create_issue
    create_issue:
      title: "Evitar impresión de Authorization y otros secretos"
      type: Security
      estimate: 1
      body: |
        Evitar impresión de Authorization y otros secretos.

  - op: create_issue
    create_issue:
      title: "Revisar registros de errores para no incluir tokens ni datos personales"
      type: Security
      estimate: 1
      body: |
        Revisar registros de errores para no incluir tokens ni datos personales.

  - op: create_issue
    create_issue:
      title: "Actualizar guía de depuración segura"
      type: Docs
      estimate: 1
      body: |
        Actualizar guía de depuración segura.

  - op: create_issue
    create_issue:
      title: "Integrar almacenamiento cifrado para sesión"
      type: Security
      estimate: 2
      body: |
        Integrar almacenamiento cifrado (EncryptedSharedPreferences o equivalente).

  - op: create_issue
    create_issue:
      title: "Implementar migración no disruptiva de token existente"
      type: Fix
      estimate: 1
      body: |
        Implementar migración no disruptiva de token existente.

  - op: create_issue
    create_issue:
      title: "Ajustar lectura y escritura de sesión en componentes afectados"
      type: Refactor
      estimate: 1
      body: |
        Ajustar lectura y escritura de sesión en componentes afectados.

  - op: create_issue
    create_issue:
      title: "Verificar inicio de sesión, renovación y cierre de sesión"
      type: Test
      estimate: 1
      body: |
        Verificar comportamiento de inicio de sesión, renovación y cierre de sesión.

  - op: create_issue
    create_issue:
      title: "Definir decisión técnica del mecanismo único de ejecución SMS"
      type: Refactor
      estimate: 2
      body: |
        Definir decisión técnica del mecanismo único de ejecución SMS.

  - op: create_issue
    create_issue:
      title: "Eliminar rutas no usadas y código muerto o comentado"
      type: Refactor
      estimate: 2
      body: |
        Eliminar rutas no usadas y código muerto o comentado.

  - op: create_issue
    create_issue:
      title: "Ajustar reprogramación en reinicio al mecanismo elegido"
      type: Fix
      estimate: 1
      body: |
        Ajustar reprogramación en reinicio al mecanismo elegido.

  - op: create_issue
    create_issue:
      title: "Validar ausencia de envíos duplicados en escenarios de reintento"
      type: Test
      estimate: 1
      body: |
        Validar ausencia de envíos duplicados en escenarios de reintento.

  - op: create_issue
    create_issue:
      title: "Usar requestCode único por mid"
      type: Fix
      estimate: 1
      body: |
        Usar requestCode único por mid (por ejemplo mid.hashCode()).

  - op: create_issue
    create_issue:
      title: "Revisar coherencia de requestCode en SmsSyncWorker y ruta adoptada"
      type: Refactor
      estimate: 1
      body: |
        Revisar coherencia de requestCode en SmsSyncWorker y ruta de envío adoptada.

  - op: create_issue
    create_issue:
      title: "Probar múltiples mensajes en cola con resultados mixtos"
      type: Test
      estimate: 1
      body: |
        Probar múltiples mensajes en cola con resultados mixtos (éxito y fallo).

  - op: create_issue
    create_issue:
      title: "Eliminar bloques comentados obsoletos"
      type: Refactor
      estimate: 1
      body: |
        Eliminar bloques comentados obsoletos.

  - op: create_issue
    create_issue:
      title: "Remover duplicidades en contrato API de renovación"
      type: Refactor
      estimate: 1
      body: |
        Remover duplicidades obvias en contrato API (renovación de token duplicada).

  - op: create_issue
    create_issue:
      title: "Registrar decisiones en bitácora técnica del sprint"
      type: Docs
      estimate: 1
      body: |
        Registrar decisiones en bitácora técnica del sprintgit