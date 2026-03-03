---
id: test-sprint-1
title: Test Sprint 1
mode: apply
executed_at: '2026-03-03T16:02:31.388Z'
---

```yaml
milestone:
  title: "Test Sprint 1"
  description: "Milestone de prueba para validar motor PM."
  due_on: "2026-03-20"

labels:
  - test
  - sprint-test

ops:
  - op: create_issue
    title: "Issue de prueba del motor PM"
    labels: ["test","sprint-test"]
    estimate: 1
    body: |
      Esta issue fue creada automáticamente desde Markdown.
      Sirve para validar el flujo completo.
```
