# ContaMovil

Aplicación de contabilidad para **Android** que funciona **sin conexión**, con base de datos local y el **nomenclador de cuentas cubano**. Pensada para llevar al teléfono el registro contable básico: plan de cuentas, comprobantes con partida doble, libros e informes.

> **Estado:** versión de pruebas (`v0.4`, pre-release). Úsala con cuidado y haz respaldos con frecuencia.

> **Aviso:** ContaMovil es un proyecto independiente. No contiene código, interfaz ni marca de ningún software comercial existente, y no sustituye el criterio de un contador. El nomenclador se basa en la Resolución 494/2016 del Ministerio de Finanzas y Precios, que puede haber sido modificada: **verifica siempre contra la versión vigente**.

## Qué hace hoy

- **Plan de cuentas** con el nomenclador cubano (entidades estatales y no estatales): grupos, subgrupos, cuentas y subcuentas obligatorias.
  - Árbol plegable con buscador por código o nombre.
  - Alta de cuentas con **sugerencia automática de cuenta madre** según el rango del nomenclador, y aviso si el código queda fuera de rango.
- **Comprobantes de operaciones** con **partida doble**: no deja guardar si los débitos no igualan a los créditos. Importes en CUP, guardados en centavos.
- **Libro diario**, **libro mayor** (con saldo anterior y saldo corriente) y **balance de comprobación**, por rango de fechas.
- **Respaldo y restauración** de toda la base de datos mediante un archivo que guardas donde quieras (Drive, correo, memoria externa). La restauración valida el archivo y conserva una copia de los datos previos.
- Base de datos con **migraciones**: las actualizaciones se instalan encima sin perder datos.

## Instalación

1. Ve a la sección [**Releases**](../../releases) y descarga el archivo `.apk` de la versión más reciente.
2. Ábrelo en tu teléfono y permite la instalación de apps de fuentes desconocidas cuando Android lo pida.
3. Requiere **Android 8.0 (API 26)** o superior.

Las versiones se firman con la misma clave, así que puedes instalar una encima de otra sin desinstalar.

## Uso rápido

1. En **Cuentas**, toca **Nomenclador** y elige el tipo de entidad (estatal o no estatal).
2. En **Comprobantes**, toca **+**, elige las cuentas, pon cada importe en débito o crédito y guarda cuando cuadre.
3. En **Informes** consulta el diario, el mayor y el balance.
4. En **Ajustes** crea un **respaldo** y guárdalo fuera del teléfono.

## Tecnología

| Área | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose + Material 3 |
| Base de datos | Room (SQLite) con migraciones |
| Arquitectura | MVVM, repositorios y módulos Gradle |
| Inyección de dependencias | Hilt |
| Integración continua | GitHub Actions |

## Estructura del repositorio

```
├── app/                    # Punto de entrada, tema y firma
├── core/database/          # Room: entidades, DAOs, migraciones, respaldo
├── feature/contabilidad/   # Pantallas, ViewModels y repositorios
├── docs/                   # Arquitectura y notas del nomenclador
└── .github/workflows/      # Compilación automática del APK
```

## Compilar desde el código

Requisitos: JDK 17, Android SDK 35 y Gradle 8.9.

```bash
gradle assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/`. También puedes dejar que **GitHub Actions** lo compile: cada ejecución sube el APK como *artifact*.

> **Seguridad:** el repositorio incluye una clave de firma de **depuración** para poder actualizar las pruebas sin desinstalar. No sirve para publicar en una tienda de aplicaciones; para eso se usará una clave privada guardada como secreto.

## Hoja de ruta

- [x] Nomenclador de cuentas cubano
- [x] Comprobantes con partida doble
- [x] Libro diario, mayor y balance de comprobación
- [x] Respaldo y restauración de datos
- [x] Editar comprobantes existentes
- [ ] Saldos acumulados por subgrupo y grupo
- [ ] Exportar informes a PDF y Excel
- [ ] Cifrado de la base de datos
- [ ] Acceso con huella o PIN
- [ ] Inventario y almacenes
- [ ] Cobros, pagos, cuentas por cobrar y por pagar
- [ ] Activos fijos, costos y nómina
- [ ] Soporte multimoneda

## Contribuir

1. Haz un fork del repositorio.
2. Crea una rama: `git checkout -b mejora/mi-cambio`
3. Haz commit de tus cambios: `git commit -m "Describe tu cambio"`
4. Sube la rama y abre un Pull Request.

Los *issues* son bienvenidos: errores, ideas o dudas sobre el nomenclador.

## Licencia

Distribuido bajo la licencia MIT. Consulta el archivo `LICENSE`.
