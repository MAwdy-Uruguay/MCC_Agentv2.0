# Template para generar comandos de sprint

Este archivo sirve como plantilla para convertir un documento
de planificación en un comando ejecutable para el motor PM.

INSTRUCCIONES PARA CODEX:

1. Leer el documento ubicado en:

documents/<archivo>

2. Convertir las tareas descritas en operaciones create_issue.

3. Generar un archivo en:

project-management/commands/<sprint>.md

Usar exactamente este formato:

---
id: <id-unico>
title: <titulo sprint>
mode: apply
---

```yaml
milestone:
  title: "<titulo>"
  description: "<descripcion>"
  due_on: "<fecha>"

labels:
  - sprint-x

ops:
  - op: create_issue
    title: "<titulo>"
    type: "Feature | Bug | Task"
    labels: ["area"]
    estimate: <1-8>
    body: |
      Descripción clara de la tarea.
```