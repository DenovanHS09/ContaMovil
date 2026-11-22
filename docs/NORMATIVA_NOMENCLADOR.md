# Normativa del nomenclador de cuentas empresarial

Registro de las disposiciones analizadas y de cómo se reflejan en ContaMovil. Fuente prioritaria: los PDF
oficiales de la Gaceta Oficial. Una disposición no se considera vigente solo por aparecer en una fuente secundaria.

## Estado de las normas (revisión v6)
| Norma | Firma / publicación | Vigencia | Estado | Verificación |
|---|---|---|---|---|
| 360/2013 y 314/2014 | 2013-09-05 / 2014-07-10 | — | Derogadas por la 1100/2015 | Solo referencia en la 1100 |
| 1100/2015 (GOC-2016-94-O5) | 2015-11-24 / 2016-02-04 | Desde el ejercicio 2016 | Derogada por la 494/2016 | Texto oficial (parcial) |
| 494/2016 (GOC-2016-977-EX39) | 2016-11-02 / 2016-12-05 | Desde el ejercicio 2017 | Modificada | Anexo 1 íntegro; Anexo 2 por fases |
| 497/2016 (GOC-2017-15-EX1) | 2016-11-02 / 2017-01-12 | Desde el ejercicio 2017 | Derogada por la 39/2023 | Texto oficial (parcial) |
| 498/2016 (GOC-2017-16-EX1) | 2016-11-02 / 2017-01-12 | No fija fecha | Modificada | Texto oficial (parcial) |
| 407/2019 (GOC-2019-871-O78) | 2019-09-16 / 2019-10-11 | 2019-10-01 | Vigente | Texto oficial íntegro |
| 166/2021 (GOC-2021-642-O75) | 2021-05-11 / 2021-07-07 | "Al cierre de enero de 2021" | Derogada por la 39/2023 | Texto oficial íntegro |
| 39/2023 (GOC-2023-270-EX20) | 2023-03-03 / 2023-03-31 | "A partir del primero de enero" | Vigente (clasificador presupuestario) | Texto oficial íntegro |

La Res. 371/2019 solo consta por la 407/2019, que la deroga; su texto no se ha localizado.

## Auditoría del Anexo 1 (frente a lo que carga la app)
- 440 cuentas y subcuentas no aseguradoras por variante: coinciden en código, naturaleza y rango.
- Nombres iguales salvo el tipo de guion en 182, 364, 841, 843 y 627 (no estatal): solo tipografía.
- 45 registros de la Res. 407/2019: sin discrepancias.
- Omitidas a propósito: cuenta 154 y secciones 50.3 y 50.4 (40 registros, uso exclusivo de seguros). Se guardan en
  el historial con ámbito `SEGUROS`, no como cuentas.
- Anexo 2: las 185 cuentas principales del Anexo 1 tienen entrada propia.
- Datos completos en `docs/datos/` (`nomenclador_consolidado.csv/json`, `comparacion_app_vs_norma.csv`).

## Cuentas 865-866 (Otros Gastos): pendiente de interpretación jurídica
La Res. 166/2021 modificó el uso y contenido (no el código, nombre ni naturaleza). La Res. 39/2023 deroga la 166
completa sin decir qué redacción rige para el Anexo 2 de la 494. ContaMovil guarda ambas redacciones como
"pendiente de interpretación" y no marca ninguna como vigente. La fecha de inicio de la versión de la 166
(2021-01-31) es aproximada: la norma dice "al cierre de enero de 2021". Se recomienda una consulta formal al MFP.

## Estructura de datos (migraciones solo aditivas)
- `normas`, `cuenta_versiones`, `cambios_normativos` (v4), `perfil_entidad` (v5).
- `normas_revisiones` (v6): cada corrección con su valor anterior, motivo y fuente, con clave única.
- Una corrección se revierte a mano restaurando `valorAnterior` del registro correspondiente.

## Límites conocidos
- El uso y contenido de las cuentas es un resumen redactado, no una transcripción. Por ahora cubre la Res.
  407/2019, la 865 y las cuentas nominales y de cierre; activos y pasivos quedan para las siguientes fases.
- No se ha leído el texto de las resoluciones 371/2019, 360/2013 y 314/2014.
- Los trabajadores por cuenta propia y las MIPYMES: ver la pantalla de Normativa (normas 351/2021, 306/2023 y
  272/2024 sin verificar o solo por ficha).
