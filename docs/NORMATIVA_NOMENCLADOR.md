# Normativa del nomenclador de cuentas empresarial

Registro de las disposiciones analizadas y de cómo se reflejan en ContaMovil.
Las fuentes son la Gaceta Oficial de la República de Cuba y, de forma orientativa, las consultas
publicadas por el MFP. Una disposición no se considera vigente solo por aparecer en una fuente secundaria.

| Norma | Estado | Verificación | Efecto en la app |
|---|---|---|---|
| Res. 1100/2015 | Derogada por la 494/2016 | Solo ficha de la Gaceta | Antecedente histórico |
| Res. 494/2016 (GOC-2016-977-EX39) | Modificada | Anexo 1 leído; Anexo 2 en parte | Nomenclador base |
| Res. 371/2019 | Derogada por la 407/2019 | Solo referencia en la 407 | No se aplica; antecedente |
| Res. 407/2019 (GOC-2019-871-O78) | Vigente desde el 1/10/2019 | Texto oficial leído | Cuentas 350, 576-580 y subcuentas de la 914 |
| Res. 166/2021 (GOC-2021-642-O75) | Derogada por la 39/2023 | Solo ficha de la Gaceta | Cuentas 865-866: pendiente de interpretación |
| Res. 39/2023 (GOC-2023-270-EX20) | Vigente | Texto oficial leído | Clasificador presupuestario: no se carga |
| Res. 497/2016 | Derogada por la 39/2023 | Solo ficha de la Gaceta | Clasificador presupuestario: no se carga |

## Pendiente de interpretación jurídica
Cuentas 865-866 (Otros Gastos), Anexo 2: la Res. 39/2023 deroga la 166/2021 completa, pero no dice
qué redacción rige para el Anexo 2 de la 494. No se asume que vuelva la original. Se recomienda una
consulta formal a la Dirección de Política Contable del MFP.

## Límites conocidos
- No se pudo leer el texto de las resoluciones 166/2021, 371/2019 y 1100/2015.
- El Anexo 2 de la 494 (uso y contenido) no se ha leído íntegro. El "uso y contenido" de la app es un
  resumen redactado, no una transcripción, y por ahora solo cubre las cuentas de la Res. 407/2019 y la 865.
- La fecha de entrada en vigor de la 494 no está verificada y se deja vacía.

## Estructura de datos (migración v3 a v4, solo aditiva)
- `normas`: una fila por disposición, con fuente, fechas, estado y grado de verificación.
- `cuenta_versiones`: qué decía el nomenclador de cada cuenta y desde cuándo (por código).
- `cambios_normativos`: qué cambió cada norma en cada cuenta, con su estado.

## Tipos de entidad (MIPYME y trabajadores por cuenta propia)
ContaMovil guarda el tipo de entidad (MIPYME privada, MIPYME estatal, trabajador por cuenta propia, otra
entidad estatal u otra no estatal). Decide la variante del grupo 30 al cargar el nomenclador y qué normas
se muestran como relacionadas.

| Norma | Estado registrado | Verificación |
|---|---|---|
| Res. 351/2021 (MIPYMES: aplican las NCIF) | Sin verificar | Solo documentos informativos |
| Res. 306/2023 (regulaciones tributarias, financieras y de precios de MIPYMES) | Sin verificar | Solo documentos informativos |
| Res. 272/2024 (contabilidad de trabajadores por cuenta propia) | Vigente | Solo ficha de la Gaceta |
| Res. 346/2021 (norma contable de trabajadores por cuenta propia) | Derogada por la 272/2024 | Solo ficha de la Gaceta |

La asignación de la variante no estatal a los trabajadores por cuenta propia es una propuesta, no una
disposición confirmada.
