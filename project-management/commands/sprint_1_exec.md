# Sprint 1 — Ejecución

```yaml
version: 1
sprint: "Sprint 1 - Seguridad y saneamiento"
operations:
  - create_issue:
      title: "Definir política por sabor y tipo de compilación para DEV, PREPROD y PROD"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Implementar la definición de política de red por sabor y tipo de compilación para DEV, PREPROD y PROD.

        Criterio de aceptación relacionado:
        - La excepción de tráfico en texto plano queda limitada y documentada por entorno.

  - create_issue:
      title: "Ajustar AndroidManifest.xml para no permitir tráfico en texto plano global"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 2
      body: |
        Ajustar AndroidManifest.xml para eliminar la habilitación global de tráfico en texto plano.

        Criterios de aceptación relacionados:
        - En producción no se permite tráfico HTTP plano.
        - La aplicación mantiene conectividad en endpoints HTTPS válidos.

  - create_issue:
      title: "Configurar network_security_config específico por entorno"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Configurar network_security_config por entorno para controlar excepciones de seguridad de red.

        Criterio de aceptación relacionado:
        - Las excepciones de texto plano quedan limitadas por entorno.

  - create_issue:
      title: "Validar conectividad HTTPS en cada entorno soportado"
      type: Test
      labels: ["sprint-1", "seguridad", "pruebas", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Ejecutar validaciones de conectividad HTTPS en DEV, PREPROD y PROD.

        Criterio de aceptación relacionado:
        - La aplicación mantiene conectividad en endpoints HTTPS válidos.

  - create_issue:
      title: "Condicionar HttpLoggingInterceptor por BuildConfig.DEBUG"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Ajustar la configuración de logging HTTP para habilitar detalles solo en compilaciones de depuración.

        Criterio de aceptación relacionado:
        - En compilación de producción no se registran cuerpos de solicitud/respuesta.

  - create_issue:
      title: "Evitar impresión de Authorization y secretos en registros"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Sanitizar registros para impedir exposición de cabeceras sensibles y secretos.

        Criterio de aceptación relacionado:
        - No se registran tokens ni cabeceras sensibles en ninguna compilación.

  - create_issue:
      title: "Revisar registros de errores para excluir tokens y datos personales"
      type: Security
      labels: ["sprint-1", "seguridad", "observabilidad", "prioridad:alta"]
      estimate: 1
      body: |
        Revisar y corregir los registros de errores para evitar filtración de datos sensibles o personales.

        Criterio de aceptación relacionado:
        - No se exponen datos sensibles en registros.

  - create_issue:
      title: "Actualizar guía de depuración segura"
      type: Docs
      labels: ["sprint-1", "seguridad", "documentacion", "prioridad:alta"]
      estimate: 1
      body: |
        Actualizar la guía de depuración segura con lineamientos de logging y tratamiento de datos sensibles.

        Criterio de aceptación relacionado:
        - Se mantiene trazabilidad útil sin comprometer seguridad.

  - create_issue:
      title: "Integrar almacenamiento cifrado para sesión"
      type: Security
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 2
      body: |
        Integrar almacenamiento cifrado para persistencia de credenciales de sesión.

        Criterio de aceptación relacionado:
        - El token no se guarda en texto plano.

  - create_issue:
      title: "Implementar migración no disruptiva del token existente"
      type: Fix
      labels: ["sprint-1", "seguridad", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Implementar migración de token previo hacia almacenamiento cifrado sin afectar sesiones activas.

        Criterio de aceptación relacionado:
        - La sesión previa se migra correctamente sin romper el inicio de sesión.

  - create_issue:
      title: "Ajustar lectura y escritura de sesión en componentes afectados"
      type: Refactor
      labels: ["sprint-1", "arquitectura", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Ajustar los componentes que consumen sesión para usar el nuevo almacenamiento seguro.

        Criterio de aceptación relacionado:
        - Inicio de sesión, renovación y cierre de sesión funcionan correctamente.

  - create_issue:
      title: "Verificar inicio, renovación y cierre de sesión con almacenamiento cifrado"
      type: Test
      labels: ["sprint-1", "pruebas", "seguridad", "prioridad:alta"]
      estimate: 1
      body: |
        Ejecutar validaciones funcionales para inicio, renovación y cierre de sesión tras la migración.

        Criterio de aceptación relacionado:
        - Cierre de sesión elimina credenciales de forma segura.

  - create_issue:
      title: "Definir decisión técnica del mecanismo único de ejecución SMS"
      type: Refactor
      labels: ["sprint-1", "arquitectura", "mensajeria", "prioridad:alta"]
      estimate: 2
      body: |
        Definir y documentar la estrategia única de ejecución de envío SMS para operación estable.

        Criterio de aceptación relacionado:
        - Solo existe un flujo activo de envío de SMS.

  - create_issue:
      title: "Eliminar rutas no usadas y código muerto/comentado del flujo SMS"
      type: Refactor
      labels: ["sprint-1", "deuda-tecnica", "mensajeria", "prioridad:alta"]
      estimate: 2
      body: |
        Eliminar rutas obsoletas y código muerto asociado al flujo alternativo de envío.

        Criterio de aceptación relacionado:
        - No hay doble programación ni competencia entre worker/servicio.

  - create_issue:
      title: "Ajustar reprogramación al reinicio con el mecanismo elegido"
      type: Fix
      labels: ["sprint-1", "mensajeria", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Ajustar la reprogramación de sincronización en reinicio para que use únicamente el flujo adoptado.

        Criterio de aceptación relacionado:
        - El flujo sigue operativo tras reinicio del dispositivo.

  - create_issue:
      title: "Validar ausencia de envíos duplicados en escenarios de reintento"
      type: Test
      labels: ["sprint-1", "pruebas", "mensajeria", "prioridad:alta"]
      estimate: 1
      body: |
        Ejecutar pruebas de reintento para comprobar que no se produzcan envíos duplicados.

        Criterio de aceptación relacionado:
        - No hay competencia entre rutas ni duplicidad de envío.

  - create_issue:
      title: "Implementar requestCode único por mid en PendingIntent"
      type: Fix
      labels: ["sprint-1", "mensajeria", "confiabilidad", "prioridad:alta"]
      estimate: 1
      body: |
        Ajustar la generación de requestCode para que sea único por mensaje y evitar colisiones.

        Criterio de aceptación relacionado:
        - Cada SMS mantiene correlación correcta con su confirmación de estado.

  - create_issue:
      title: "Revisar coherencia de PendingIntent en SmsSyncWorker y ruta adoptada"
      type: Refactor
      labels: ["sprint-1", "mensajeria", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Validar y unificar el patrón de construcción de PendingIntent en la ruta de envío vigente.

        Criterio de aceptación relacionado:
        - No se observan colisiones de intents entre mensajes.

  - create_issue:
      title: "Probar envío por lotes con resultados mixtos para detectar colisiones"
      type: Test
      labels: ["sprint-1", "pruebas", "mensajeria", "prioridad:alta"]
      estimate: 1
      body: |
        Probar escenarios de envío por lotes con éxitos y fallos combinados para verificar correlación de estados.

        Criterio de aceptación relacionado:
        - No se observan colisiones en pruebas con lotes de mensajes.

  - create_issue:
      title: "Eliminar bloques comentados obsoletos del código fuente"
      type: Refactor
      labels: ["sprint-1", "deuda-tecnica", "android", "prioridad:alta"]
      estimate: 1
      body: |
        Retirar implementaciones comentadas que ya no forman parte del diseño vigente.

        Criterio de aceptación relacionado:
        - Código fuente sin bloques comentados de implementaciones viejas.

  - create_issue:
      title: "Remover duplicidades en contrato API de renovación de token"
      type: Refactor
      labels: ["sprint-1", "deuda-tecnica", "arquitectura", "prioridad:alta"]
      estimate: 1
      body: |
        Unificar métodos redundantes de renovación en la interfaz API para reducir complejidad.

        Criterio de aceptación relacionado:
        - Contrato de API sin duplicidades funcionales innecesarias.

  - create_issue:
      title: "Registrar decisiones técnicas del sprint en bitácora"
      type: Docs
      labels: ["sprint-1", "documentacion", "arquitectura", "prioridad:alta"]
      estimate: 1
      body: |
        Documentar decisiones de arquitectura y seguridad adoptadas durante el sprint para trazabilidad.

        Criterio de aceptación relacionado:
        - Revisión de pares aprobada por legibilidad y mantenibilidad.
```
