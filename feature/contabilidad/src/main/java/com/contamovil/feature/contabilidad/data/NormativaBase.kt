package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.EstadoNorma
import com.contamovil.core.database.entity.EstadoNormativo
import com.contamovil.core.database.entity.InstrumentoNorma
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.TipoCambio
import com.contamovil.core.database.entity.VerificacionNorma

/** Resumen (en palabras propias, no transcripción) del uso y funcionamiento de una cuenta. */
data class ResumenUso(
    val uso: String,
    val debitos: String? = null,
    val creditos: String? = null
)

/**
 * Datos normativos del nomenclador de cuentas empresarial cubano y su trazabilidad.
 *
 * Criterios:
 *  - Solo se registra como confirmado lo comprobado en el texto oficial o en la ficha de la
 *    Gaceta Oficial. Lo demás queda marcado como no verificado o pendiente de interpretación.
 *  - Las fechas desconocidas se dejan vacías; no se estiman.
 *  - El clasificador por objeto del gasto del Presupuesto del Estado es un instrumento distinto
 *    y nunca se carga como cuentas empresariales.
 */
object NormativaBase {
    const val R1100 = "RES-1100-2015"
    const val R494 = "RES-494-2016"
    const val R497 = "RES-497-2016"
    const val R498 = "RES-498-2016"
    const val R500 = "RES-500-2016"
    const val R371 = "RES-371-2019"
    const val R407 = "RES-407-2019"
    const val R166 = "RES-166-2021"
    const val R39 = "RES-39-2023"
    const val R346 = "RES-346-2021"
    const val R351 = "RES-351-2021"
    const val R272 = "RES-272-2024"
    const val R306 = "RES-306-2023"

    private const val MFP = "Ministerio de Finanzas y Precios"
    private const val VIGENCIA_407 = "2019-10-01"
    private const val FIN_ANTES_407 = "2019-09-30"

    val normas: List<NormaEntity> = listOf(
        NormaEntity(
            codigo = R1100, numero = 1100, anio = 2015, organismo = MFP,
            titulo = "Antecedente histórico de la Resolución 494/2016",
            gaceta = "No verificada",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = R494,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "La ficha de la Gaceta Oficial indica que la Res. 494/2016 la deroga. " +
                "Se conserva solo como antecedente; su texto no se ha revisado."
        ),
        NormaEntity(
            codigo = R494, numero = 494, anio = 2016, organismo = MFP,
            titulo = "Nomenclador de Cuentas de la actividad empresarial, unidades presupuestadas " +
                "de tratamiento especial y sector cooperativo (Anexo 1 nomenclador; Anexo 2 uso y contenido)",
            gaceta = "Gaceta Oficial No. 39 Extraordinaria de 2016", identificador = "GOC-2016-977-EX39",
            fechaFirma = "2016-11-02",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.MODIFICADA,
            verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Anexo 1 leído; Anexo 2 revisado solo en parte. Fecha de entrada en vigor no verificada " +
                "(una fuente secundaria da la publicación el 5/12/2016, sin confirmar). " +
                "La ficha de la Gaceta la lista como modificada por las resoluciones 407/2019 y 166/2021."
        ),
        NormaEntity(
            codigo = R497, numero = 497, anio = 2016, organismo = MFP,
            titulo = "Clasificador por Objeto del Gasto del Presupuesto del Estado (instrumento presupuestario)",
            gaceta = "Gaceta Oficial Extraordinaria de 2017 (según el identificador de la ficha)",
            identificador = "GOC-2017-15-EX1", fechaFirma = "2016-11-02",
            instrumento = InstrumentoNorma.CLASIFICADOR_OBJETO_GASTO,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = R39,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "No es el nomenclador empresarial: no se carga como cuentas. " +
                "Derogada por la Res. 39/2023."
        ),
        NormaEntity(
            codigo = R498, numero = 498, anio = 2016, organismo = MFP,
            titulo = "Proformas de estados financieros de la actividad empresarial (relacionada; fuera del alcance de las cuentas)",
            gaceta = "Gaceta Oficial No. 1 Extraordinaria de 2017", identificador = "GOC-2017-16-EX1",
            fechaFirma = "2016-11-02",
            instrumento = InstrumentoNorma.PROFORMAS_ESTADOS_FINANCIEROS,
            estado = EstadoNorma.MODIFICADA,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "Modificada, entre otras, por la Res. 407/2019. Se registra solo como norma relacionada."
        ),
        NormaEntity(
            codigo = R500, numero = 500, anio = 2016, organismo = MFP,
            titulo = "Nomenclador de la actividad presupuestada (instrumento distinto del empresarial)",
            gaceta = "Gaceta Oficial No. 39 Extraordinaria de 2016 (según consulta del MFP; sin verificar)",
            instrumento = InstrumentoNorma.NOMENCLADOR_PRESUPUESTADO,
            estado = EstadoNorma.SIN_VERIFICAR,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Citada por el MFP en sus consultas como nomenclador de la actividad presupuestada. " +
                "No se carga en ContaMovil."
        ),
        NormaEntity(
            codigo = R371, numero = 371, anio = 2019, organismo = MFP,
            titulo = "Modificaba las resoluciones 494 y 498 de 2016; derogada por la Res. 407/2019",
            gaceta = "No registrada en la ficha de la Gaceta de la Res. 494/2016",
            fechaFirma = "2019-08-16",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = R407,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por los considerandos de la Res. 407/2019: tenía errores en la descripción y " +
                "el contenido de la cuenta por cobrar con sentencia firme. No se aplica como modificación " +
                "vigente independiente. Su texto no se ha localizado."
        ),
        NormaEntity(
            codigo = R407, numero = 407, anio = 2019, organismo = MFP,
            titulo = "Modifica los anexos 1 y 2 de la Resolución 494/2016: incorpora las cuentas 350, 576 a 580 y " +
                "las subcuentas de la 914; modifica también la Res. 498/2016",
            gaceta = "Gaceta Oficial No. 78 Ordinaria de 2019", identificador = "GOC-2019-871-O78",
            fechaFirma = "2019-09-16", fechaPublicacion = "2019-10-11", fechaVigencia = VIGENCIA_407,
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.VIGENTE,
            verificacion = VerificacionNorma.TEXTO_OFICIAL_LEIDO,
            observaciones = "Texto oficial leído íntegro. Entra en vigor el 1/10/2019, antes de su publicación. " +
                "Deroga la Res. 371/2019. Para la proforma EFE 5920-04 (Res. 498/2016) ordena sumar la 350 " +
                "con la 349 y las 576 a 580 con las 565 a 568, con desglose en notas."
        ),
        NormaEntity(
            codigo = R166, numero = 166, anio = 2021, organismo = MFP,
            titulo = "Modifica el Anexo 2 de la Res. 494/2016 (cuentas 865-866) y la Res. 497/2016",
            gaceta = "Gaceta Oficial No. 75 Ordinaria de 2021", identificador = "GOC-2021-642-O75",
            fechaFirma = "2021-05-11", fechaPublicacion = "2021-07-07",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = R39,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "Texto no verificado. La fecha de firma procede de la Res. 39/2023. " +
                "Derogada completa por la Res. 39/2023, que no dice nada sobre su efecto en el Anexo 2 de la 494: " +
                "queda pendiente de interpretación jurídica."
        ),
        NormaEntity(
            codigo = R39, numero = 39, anio = 2023, organismo = MFP,
            titulo = "Aprueba el Clasificador por Objeto del Gasto del Presupuesto del Estado y deroga las " +
                "resoluciones 497/2016 y 166/2021",
            gaceta = "Gaceta Oficial No. 20 Extraordinaria de 2023", identificador = "GOC-2023-270-EX20",
            fechaFirma = "2023-03-03", fechaPublicacion = "2023-03-31",
            instrumento = InstrumentoNorma.CLASIFICADOR_OBJETO_GASTO,
            estado = EstadoNorma.VIGENTE,
            verificacion = VerificacionNorma.TEXTO_OFICIAL_LEIDO,
            observaciones = "Texto oficial leído. Pertenece a la contabilidad gubernamental: no es el nomenclador " +
                "empresarial y no se carga como cuentas. No menciona la Res. 494/2016. Se aplica a operaciones " +
                "desde el primero de enero (el año no se precisa en el texto)."
        ),
        NormaEntity(
            codigo = R346, numero = 346, anio = 2021, organismo = MFP,
            titulo = "Norma Específica de Contabilidad de la Actividad de Trabajo por Cuenta Propia (antecedente)",
            gaceta = "Gaceta Oficial No. 94 Ordinaria de 2021 (según fuentes secundarias)",
            instrumento = InstrumentoNorma.NORMA_CONTABLE,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = R272,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "La ficha de la Res. 272/2024 indica que la deroga. Hoy los trabajadores por cuenta propia " +
                "que llevan contabilidad aplican las Normas Cubanas de Información Financiera de la actividad empresarial."
        ),
        NormaEntity(
            codigo = R351, numero = 351, anio = 2021, organismo = MFP,
            titulo = "Dispone que las MIPYMES apliquen las Normas Cubanas de Información Financiera " +
                "para el registro contable de sus operaciones",
            gaceta = "Gaceta Oficial No. 94 Ordinaria de 2021",
            fechaFirma = "2021-08-14", fechaPublicacion = "2021-08-19",
            instrumento = InstrumentoNorma.NORMA_CONTABLE,
            estado = EstadoNorma.SIN_VERIFICAR,
            verificacion = VerificacionNorma.SOLO_DOCUMENTO_INFORMATIVO,
            observaciones = "Texto no leído. Según documentos informativos del MEP y del MFP, las MIPYMES usan el " +
                "nomenclador y los demás instrumentos contables de la actividad empresarial y emiten sus estados " +
                "financieros. Una fuente secundaria añade que autoriza usar la cuenta 600 para los aportes de los " +
                "socios (sin verificar). Su estado en la Gaceta no se ha comprobado."
        ),
        NormaEntity(
            codigo = R306, numero = 306, anio = 2023, organismo = MFP,
            titulo = "Regulaciones tributarias, financieras y de precios de las MIPYMES",
            gaceta = "No verificada",
            fechaFirma = "2023-12-30",
            instrumento = InstrumentoNorma.OTRA_RELACIONADA,
            estado = EstadoNorma.SIN_VERIFICAR,
            verificacion = VerificacionNorma.SOLO_DOCUMENTO_INFORMATIVO,
            observaciones = "Fecha según un índice normativo secundario. Texto no leído: no se sabe si afecta a la " +
                "parte contable."
        ),
        NormaEntity(
            codigo = R272, numero = 272, anio = 2024, organismo = MFP,
            titulo = "Actualiza la contabilidad de las operaciones de los trabajadores por cuenta propia según " +
                "sus ingresos anuales (deroga la Res. 346/2021)",
            gaceta = "Gaceta Oficial No. 78 Ordinaria de 2024", identificador = "GOC-2024-454-O78",
            fechaPublicacion = "2024-08-19",
            instrumento = InstrumentoNorma.NORMA_CONTABLE,
            estado = EstadoNorma.VIGENTE,
            verificacion = VerificacionNorma.SOLO_FICHA_GACETA,
            observaciones = "La ficha de la Gaceta no registra modificaciones ni derogación. Según un resumen de la norma, " +
                "obliga a llevar contabilidad a quienes superan 500 000 CUP anuales de ingresos. El MFP (Dirección " +
                "de Política Contable) confirma en una consulta que estos trabajadores usan el nomenclador de la " +
                "Res. 494/2016. Texto no leído; la fecha de publicación procede de una consulta del MFP."
        )
    )

    private val CUENTAS_407 = setOf("350", "576", "577", "578", "579", "580")

    /** Norma que dio origen a un código de cuenta o subcuenta. */
    fun normaDe(codigo: String): String =
        if (codigo.substringBefore('.') in CUENTAS_407 || codigo.startsWith("914.")) R407 else R494

    private val S_UNO = "Se analiza por cliente y por documento que origina la obligación; " +
        "exige las subcuentas obligatorias del nomenclador."

    /** Resúmenes redactados a partir del texto íntegro de la Res. 407/2019. */
    private val RESUMENES_407: Map<String, ResumenUso> = mapOf(
        "350" to ResumenUso(
            uso = "Cuentas por cobrar respaldadas por una sentencia firme del tribunal que, por distintas " +
                "causas, no se han podido cobrar. $S_UNO",
            debitos = "Al recibirse la sentencia firme cuando el cobro no ha sido posible.",
            creditos = "Por los cobros realizados o por la cancelación del adeudo."
        ),
        "576" to ResumenUso(
            uso = "Efectos por pagar en reclamación o litigio entre las partes, sin intervención de tribunales. $S_UNO",
            debitos = "Por los pagos hechos, por su paso a Efectos por Pagar Protestados o por su cancelación.",
            creditos = "Al iniciarse el proceso de reclamación del adeudo."
        ),
        "577" to ResumenUso(
            uso = "Cuentas por pagar en reclamación o litigio entre las partes, sin intervención de tribunales. $S_UNO",
            debitos = "Por los pagos hechos, por su paso a Cuentas por Pagar en Proceso Judicial o por su cancelación.",
            creditos = "Al iniciarse el proceso de reclamación del adeudo."
        ),
        "578" to ResumenUso(
            uso = "Efectos por pagar cuya reclamación ya está en proceso judicial. $S_UNO",
            debitos = "Por los pagos hechos, por su paso a Cuentas por Pagar con Sentencia Firme o por su cancelación.",
            creditos = "Al iniciarse el proceso legal de reclamación del adeudo."
        ),
        "579" to ResumenUso(
            uso = "Cuentas por pagar cuya reclamación ya está en proceso judicial. $S_UNO",
            debitos = "Por los pagos hechos, por su paso a Cuentas por Pagar con Sentencia Firme o por su cancelación.",
            creditos = "Al iniciarse el proceso legal de reclamación del adeudo."
        ),
        "580" to ResumenUso(
            uso = "Cuentas por pagar con sentencia firme del tribunal que no se han podido pagar a sus " +
                "beneficiarios. $S_UNO",
            debitos = "Por los pagos hechos o por la cancelación del adeudo.",
            creditos = "Al recibirse la sentencia firme cuando el pago no ha sido posible."
        ),
        "914" to ResumenUso(
            uso = "Ventas de bienes destinados a la exportación, analizadas según el clasificador de productos " +
                "vigente; incluye a productores y exportadores, con subcuentas obligatorias. " +
                "Se analiza por cliente y por documento.",
            debitos = "Su saldo se cancela al cierre del ejercicio contra la cuenta Resultado."
        )
    )

    private const val USO_865_ORIGINAL =
        "Redacción original de la Res. 494/2016: gastos que no corresponden a las actividades " +
            "fundamentales de la entidad y que no son gastos financieros, pérdidas, faltantes ni gastos de años anteriores."

    private val MARCA_RANGO = Regex("""\s*\[\d+-\d+]\s*$""")

    private fun ubicar(c: CuentaBase, mapa: Map<String, CuentaBase>): Pair<String, String> {
        val cadena = mutableListOf(c.codigo)
        var padre = c.padre
        while (padre != null) {
            cadena.add(padre)
            padre = mapa[padre]?.padre
        }
        cadena.reverse()
        val grupo = cadena[0]
        val subgrupo = if (cadena.size > 1 && cadena[1].contains('.')) cadena[1] else grupo
        return grupo to subgrupo
    }

    /** Versiones iniciales de todas las cuentas del nomenclador, con su norma de origen. */
    fun versiones(): List<CuentaVersionEntity> {
        val filas = mutableListOf<CuentaVersionEntity>()
        for (variante in VarianteNomenclador.entries) {
            val plan = PlanBase.cuentas(variante)
            val mapa = plan.associateBy { it.codigo }
            for (c in plan) {
                val (grupo, subgrupo) = ubicar(c, mapa)
                val esPatrimonio = grupo == "30"
                if (variante == VarianteNomenclador.NO_ESTATAL && !esPatrimonio) continue

                val norma = normaDe(c.codigo)
                val resumen = RESUMENES_407[c.codigo]
                val base = CuentaVersionEntity(
                    cuentaCodigo = c.codigo,
                    variante = if (esPatrimonio) variante.name else null,
                    denominacion = c.nombre.replace(MARCA_RANGO, ""),
                    naturaleza = c.naturaleza,
                    grupo = grupo,
                    subgrupo = subgrupo,
                    normaOrigenCodigo = norma,
                    vigenteDesde = if (norma == R407) VIGENCIA_407 else null,
                    estadoNormativo = EstadoNormativo.CONFIRMADO
                )
                when (c.codigo) {
                    "914" -> {
                        filas += base.copy(
                            vigenteHasta = FIN_ANTES_407,
                            observaciones = "Versión original de la Res. 494/2016, sustituida desde el 1/10/2019 " +
                                "por la de la Res. 407/2019 (añade subcuentas y contenido propio)."
                        )
                        filas += base.copy(
                            normaOrigenCodigo = R407,
                            vigenteDesde = VIGENCIA_407,
                            usoContenido = resumen?.uso,
                            funcionamientoDebitos = resumen?.debitos,
                            funcionamientoCreditos = resumen?.creditos
                        )
                    }
                    "865" -> filas += base.copy(
                        usoContenido = USO_865_ORIGINAL,
                        estadoNormativo = EstadoNormativo.PENDIENTE_INTERPRETACION,
                        observaciones = "El contenido (Anexo 2) está pendiente de interpretación jurídica: la Res. " +
                            "166/2021 lo modificó y fue derogada por la Res. 39/2023 sin disponer expresamente " +
                            "qué redacción rige. Rango 865-866."
                    )
                    else -> filas += base.copy(
                        usoContenido = resumen?.uso,
                        funcionamientoDebitos = resumen?.debitos,
                        funcionamientoCreditos = resumen?.creditos
                    )
                }
            }
        }
        return filas
    }

    val cambios: List<CambioNormativoEntity> = buildList {
        add(
            CambioNormativoEntity(
                cuentaCodigo = "350", normaCodigo = R407, tipoCambio = TipoCambio.ALTA_CUENTA,
                valorNuevo = "Cuentas por Cobrar con Sentencia Firme (deudora), subcuentas obligatorias 0010 a 0060",
                estado = EstadoNormativo.CONFIRMADO,
                observaciones = "Grupo Otros Activos (10.5)."
            )
        )
        for ((codigo, nombre) in listOf(
            "576" to "Efectos por Pagar en Litigio",
            "577" to "Cuentas por Pagar en Litigio",
            "578" to "Efectos por Pagar Protestados",
            "579" to "Cuentas por Pagar en Proceso Judicial",
            "580" to "Cuentas por Pagar con Sentencia Firme"
        )) {
            add(
                CambioNormativoEntity(
                    cuentaCodigo = codigo, normaCodigo = R407, tipoCambio = TipoCambio.ALTA_CUENTA,
                    valorNuevo = "$nombre (acreedora), subcuentas obligatorias 0010 a 0060",
                    estado = EstadoNormativo.CONFIRMADO,
                    observaciones = "Grupo Otros Pasivos (20.4)."
                )
            )
        }
        add(
            CambioNormativoEntity(
                cuentaCodigo = "914", normaCodigo = R407, tipoCambio = TipoCambio.ALTA_SUBCUENTAS,
                valorAnterior = "Sin subcuentas obligatorias en el Anexo 1 leído de la Res. 494/2016",
                valorNuevo = "0010 Productores; 0020 Exportadores",
                estado = EstadoNormativo.CONFIRMADO
            )
        )
        add(
            CambioNormativoEntity(
                cuentaCodigo = "914", normaCodigo = R407, tipoCambio = TipoCambio.MODIFICA_USO_CONTENIDO,
                valorAnterior = "Según la transcripción consultada de la 494, remitía al contenido definido " +
                    "en las cuentas nominales acreedoras",
                valorNuevo = "Contenido propio: análisis por clasificador de productos, productores y exportadores",
                estado = EstadoNormativo.CONFIRMADO
            )
        )
        add(
            CambioNormativoEntity(
                cuentaCodigo = "865", normaCodigo = R166, tipoCambio = TipoCambio.MODIFICA_USO_CONTENIDO,
                valorAnterior = USO_865_ORIGINAL,
                valorNuevo = null,
                estado = EstadoNormativo.PENDIENTE_INTERPRETACION,
                observaciones = "Rango 865-866 (Otros Gastos). Texto de la Res. 166/2021 no verificado. " +
                    "Derogada por la Res. 39/2023 sin disposición expresa sobre el Anexo 2 de la 494. " +
                    "No se asume que la redacción original esté vigente: se recomienda consulta formal al MFP."
            )
        )
    }
}
