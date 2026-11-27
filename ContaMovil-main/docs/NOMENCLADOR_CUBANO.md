# Nomenclador de cuentas cubano

Fuente: Resolución 494/2016 del Ministerio de Finanzas y Precios (Anexo 1: Nomenclador de
Cuentas para la actividad empresarial, unidades presupuestadas de tratamiento especial y el
sector cooperativo agropecuario y no agropecuario).

> La Gaceta Oficial marca esta resolución como "modificada". Antes de usar la app para
> contabilidad real, contrasta el contenido con la versión vigente del MFP.

## Estructura

| Grupo | Contenido |
|---|---|
| 10 | Activos: circulantes (10.1), largo plazo (10.2), fijos (10.3), diferidos (10.4), otros (10.5), reguladoras (10.6) |
| 20 | Pasivos: circulantes (20.1), largo plazo (20.2), diferidos (20.3), otros (20.4) |
| 30 | Patrimonio neto (entidades estatales) o Capital contable (no estatales, mercantiles, cooperativas) |
| 40 | Gastos de producción |
| 50 | Cuentas nominales: deudoras (50.1) y acreedoras (50.2) |
| 60 | Cuenta de cierre (999 Resultado) |

## Reglas que la app respeta

- Las cuentas por rango ("101 a 108 Efectivo en Caja") se cargan con el primer código y el
  rango aparece entre corchetes en el nombre. Los demás códigos del rango los abre la entidad.
- Las subcuentas de uso obligatorio se cargan como `cuenta.subcuenta` (ej. `135.0010`).
  Una cuenta con subcuentas no acepta movimientos directos.
- La norma autoriza codificar sin ceros a la izquierda en sistemas informáticos
  (0010 puede ser 10). Por ahora se conservan los ceros.
- Naturaleza: deudora, acreedora o mixta, según el nomenclador.

## Pendiente

- Cuentas de uso exclusivo de empresas de seguro (50.3 y 50.4): no incluidas.
- Validar que un código nuevo pertenezca a un rango válido (requiere guardar el rango en la
  base de datos, con migración).
- Anexo 2 (uso y contenido de cada cuenta): se puede enlazar como ayuda en la app.
