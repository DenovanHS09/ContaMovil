# Arquitectura

- **app**: punto de entrada, Hilt, navegación.
- **core:database**: Room (entidades, DAOs, migraciones) y módulo Hilt.
- **feature:contabilidad**: repositorio, ViewModels y pantallas de Compose.

Regla: los módulos `feature:*` dependen de `core:*`, nunca entre sí.
Los esquemas de Room se exportan a `core/database/schemas/` y se versionan.

## Próximos pasos
1. Entidades de comprobante y asiento (partida doble: débito = crédito).
2. Libro diario y mayor como consultas Room.
3. Módulos `core:ui` y `core:common`.
4. Navegación entre módulos (Navigation Compose).
