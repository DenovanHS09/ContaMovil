# ContaMovil

Sistema de gestión contable, financiera y de inventario para **Android**, con base de datos local y soporte offline. Pensado para llevar al teléfono las funciones que normalmente se usan en un sistema de escritorio de gestión empresarial.

> ⚠️ **Nota:** ContaMovil es un proyecto independiente. No contiene código, interfaz ni marca de ningún software comercial existente.

## Estado del proyecto

🚧 En desarrollo temprano (fase 0: estructura base).

## Características previstas

- **Base de datos local** con Room (SQLite) y cifrado opcional (SQLCipher)
- **Funcionamiento offline** con sincronización opcional a un servidor
- **Usuarios y roles**, con acceso biométrico
- **Contabilidad:** plan de cuentas, comprobantes, libro diario, libro mayor, balance de comprobación
- **Inventario y almacenes:** entradas, salidas, transferencias, existencias
- **Finanzas:** cobros, pagos, cuentas por cobrar y por pagar, caja y banco
- **Activos fijos, costos y nómina**
- **Reportes:** exportación a PDF, Excel y CSV
- **Respaldo y restauración** local y en la nube
- **Extras móviles:** escáner de códigos de barras, notificaciones, tema oscuro

## Stack tecnológico

| Área | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Base de datos | Room + SQLite (SQLCipher opcional) |
| Arquitectura | MVVM + Clean Architecture |
| Inyección de dependencias | Hilt |
| Red | Ktor / Retrofit |
| Tareas en segundo plano | WorkManager |
| CI | GitHub Actions |

## Requisitos

- Android Studio (versión estable más reciente)
- JDK 17
- Android SDK con `minSdk 26` (Android 8.0) o superior

## Estructura del repositorio

```
contamovil/
├── app/                 # Módulo principal (navegación, DI)
├── core/
│   ├── database/        # Room, DAOs, migraciones
│   ├── network/         # Cliente API y sincronización
│   ├── ui/              # Tema y componentes compartidos
│   └── common/          # Utilidades
├── feature/
│   ├── contabilidad/
│   ├── inventario/
│   ├── finanzas/
│   └── ...
├── docs/                # Documentación y decisiones de diseño
├── .github/workflows/   # Integración continua
├── README.md
├── LICENSE
└── .gitignore
```

## Cómo empezar

```bash
git clone https://github.com/TU_USUARIO/contamovil.git
cd contamovil
```

1. Abre la carpeta en Android Studio.
2. Espera a que Gradle sincronice.
3. Ejecuta la app en un emulador o dispositivo (`Run ▶`).

## Hoja de ruta

- [ ] **Fase 0:** estructura del proyecto, CI, tema base
- [ ] **Fase 1:** usuarios, roles, login biométrico, catálogos
- [ ] **Fase 2:** contabilidad (plan de cuentas, comprobantes, libros)
- [ ] **Fase 3:** inventario y almacenes
- [ ] **Fase 4:** finanzas (cobros, pagos, caja y banco)
- [ ] **Fase 5:** activos fijos, costos y nómina
- [ ] **Fase 6:** sincronización, respaldos, exportación de reportes

## Cómo contribuir

1. Haz un fork del repositorio.
2. Crea una rama: `git checkout -b feature/mi-mejora`
3. Haz commit de tus cambios: `git commit -m "Añade mi mejora"`
4. Sube la rama: `git push origin feature/mi-mejora`
5. Abre un Pull Request.

Revisa los *issues* abiertos para ver en qué se puede ayudar.

## Licencia

Distribuido bajo la licencia MIT. Consulta el archivo `LICENSE` para más información.
