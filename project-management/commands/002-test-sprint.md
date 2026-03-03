---
id: test-sprint-3
title: Test Sprint 3
mode: apply
---

```yaml
milestone:
  title: "Test Sprint 2"
  description: "Milestone de prueba para validar motor PM 002."

labels:
  - test
  - sprint-test

ops:
  - op: create_issue
    title: "Issue de prueba del motor PM 2"
    labels: ["test","sprint-test"]
    estimate: 1
    body: |
      Esta issue fue creada automáticamente desde Markdown.
      Sirve para validar el flujo completo y ver que pasa.
```
