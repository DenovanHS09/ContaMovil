package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.EstadoNorma
import com.contamovil.core.database.entity.EstadoNormativo
import com.contamovil.core.database.entity.InstrumentoNorma
import com.contamovil.core.database.entity.Naturaleza
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.VerificacionNorma

/** Registro del Anexo 1 de la Res. 494/2016 de uso exclusivo de empresas de seguro. */
data class RegistroSeguro(
    val codigo: String,
    val hasta: String?,
    val nombre: String,
    val naturaleza: Naturaleza,
    val grupo: String,
    val subgrupo: String
)

data class CorreccionDenominacion(
    val codigo: String,
    val variante: String?,
    val anterior: String,
    val oficial: String
)

/**
 * Revisión v6 de los datos normativos, hecha contra los PDF oficiales de la Gaceta Oficial
 * (1100/2015, 494/2016, 497 y 498/2016, 407/2019, 166/2021 y 39/2023).
 * Todo se aplica de forma aditiva o con registro de lo anterior en `normas_revisiones`.
 */
object NormativaRevision {
    const val R360 = "RES-360-2013"
    const val R314 = "RES-314-2014"
    const val R354 = "RES-354-2013"
    const val R1173 = "RES-1173-2015"
    const val R138 = "RES-138-2016"
    const val R499 = "RES-499-2016"

    /** Inicio del ejercicio contable 2017: desde cuándo se aplica la Res. 494/2016 (disposición séptima). */
    const val DESDE_494 = "2017-01-01"
    /** Fecha aproximada: la Res. 166/2021 se aplica "al cierre de enero de 2021". */
    const val DESDE_166_APROX = "2021-01-31"

    private const val MFP = "Ministerio de Finanzas y Precios"

    val normasNuevas: List<NormaEntity> = listOf(
        NormaEntity(
            codigo = R360, numero = 360, anio = 2013, organismo = MFP,
            titulo = "Nomenclador de la actividad empresarial y sector cooperativo (antecedente de la Res. 1100/2015)",
            gaceta = "No verificada", fechaFirma = "2013-09-05",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = NormativaBase.R1100,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por la Res. 1100/2015, que la deroga. Fue modificada por la Res. 314/2014. " +
                "Antecedente histórico: sus cuentas no se incorporan."
        ),
        NormaEntity(
            codigo = R314, numero = 314, anio = 2014, organismo = MFP,
            titulo = "Modificó la Res. 360/2013 (antecedente de la Res. 1100/2015)",
            gaceta = "No verificada", fechaFirma = "2014-07-10",
            instrumento = InstrumentoNorma.NOMENCLADOR_EMPRESARIAL,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = NormativaBase.R1100,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por la Res. 1100/2015, que la deroga. Antecedente histórico."
        ),
        NormaEntity(
            codigo = R354, numero = 354, anio = 2013, organismo = MFP,
            titulo = "Clasificador por Objeto del Gasto del Presupuesto del Estado (antecedente de la Res. 497/2016)",
            gaceta = "No verificada", fechaFirma = "2013-09-05",
            instrumento = InstrumentoNorma.CLASIFICADOR_OBJETO_GASTO,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = NormativaBase.R497,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por la Res. 497/2016, que la deroga. Instrumento presupuestario: no es el nomenclador empresarial."
        ),
        NormaEntity(
            codigo = R1173, numero = 1173, anio = 2015, organismo = MFP,
            titulo = "Norma Específica de Contabilidad No. 5, proformas de estados financieros (antecedente de la Res. 498/2016)",
            gaceta = "No verificada", fechaFirma = "2015-12-02",
            instrumento = InstrumentoNorma.PROFORMAS_ESTADOS_FINANCIEROS,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = NormativaBase.R498,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por la Res. 498/2016, que la deroga."
        ),
        NormaEntity(
            codigo = R138, numero = 138, anio = 2016, organismo = MFP,
            titulo = "Proformas de estados financieros (antecedente de la Res. 498/2016)",
            gaceta = "No verificada", fechaFirma = "2016-04-01",
            instrumento = InstrumentoNorma.PROFORMAS_ESTADOS_FINANCIEROS,
            estado = EstadoNorma.DEROGADA, derogadaPorCodigo = NormativaBase.R498,
            verificacion = VerificacionNorma.SOLO_REFERENCIA_EN_OTRA_NORMA,
            observaciones = "Consta por la Res. 498/2016, que la deroga."
        ),
        NormaEntity(
            codigo = R499, numero = 499, anio = 2016, organismo = MFP,
            titulo = "Norma Específica de Contabilidad de la Actividad Presupuestada No. 2 (proformas de unidades presupuestadas)",
            gaceta = "Gaceta Oficial No. 1 Extraordinaria de 2017", identificador = "GOC-2017-17-EX1",
            fechaFirma = "2016-11-02", fechaPublicacion = "2017-01-12", fechaVigencia = "2017-01-01",
            instrumento = InstrumentoNorma.NORMA_CONTABLE,
            estado = EstadoNorma.SIN_VERIFICAR,
            verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Pertenece a la contabilidad presupuestaria: no se carga en ContaMovil. Según su texto entra en " +
                "vigor el 1/01/2017 y deroga la Res. 59/2016. Su estado actual no se ha comprobado."
        )
    )

    /** Normas existentes cuyos metadatos se completan con lo verificado en los PDF oficiales. */
    val codigosAjustados = listOf(
        NormativaBase.R1100, NormativaBase.R494, NormativaBase.R497, NormativaBase.R498, NormativaBase.R166
    )

    fun ajustarNorma(n: NormaEntity): NormaEntity = when (n.codigo) {
        NormativaBase.R1100 -> n.copy(
            gaceta = "Gaceta Oficial No. 5 Ordinaria de 2016", identificador = "GOC-2016-94-O5",
            fechaFirma = "2015-11-24", fechaPublicacion = "2016-02-04", fechaVigencia = "2016-01-01",
            verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Texto oficial leído (parte dispositiva y estructura). Actualiza la Sección IV de la Res. " +
                "235/2005 (anexos 1 y 2), excluye los impuestos del costo de producción y deroga las resoluciones " +
                "360/2013 y 314/2014. Se aplica a las operaciones desde el inicio del ejercicio 2016. La Res. " +
                "494/2016 la deroga. Antecedente histórico: sus cuentas no se incorporan como vigentes."
        )
        NormativaBase.R494 -> n.copy(
            fechaPublicacion = "2016-12-05", fechaVigencia = DESDE_494,
            verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Anexo 1 verificado íntegro contra el PDF oficial: las 440 cuentas y subcuentas no " +
                "aseguradoras coinciden con la app. Anexo 2: sus 185 cuentas principales tienen entrada propia; " +
                "el uso y contenido se carga por fases en resumen. Publicada el 5/12/2016; se aplica a las " +
                "operaciones desde el inicio del ejercicio contable 2017. El Anexo 2 aclara que las exigencias " +
                "de revelación de las Normas Cubanas de Contabilidad prevalecen sobre el uso y contenido del " +
                "nomenclador. La ficha de la Gaceta la lista como modificada por las resoluciones 407/2019 y 166/2021."
        )
        NormativaBase.R497 -> n.copy(
            gaceta = "Gaceta Oficial No. 1 Extraordinaria de 2017", fechaPublicacion = "2017-01-12",
            fechaVigencia = "2017-01-01", verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Texto oficial leído (parte dispositiva y estructura). Se aplica desde el ejercicio contable " +
                "2017 y deroga la Res. 354/2013. La Res. 166/2021 la modificó y la Res. 39/2023 la deroga. " +
                "Clasificador presupuestario: no se carga como cuentas."
        )
        NormativaBase.R498 -> n.copy(
            fechaPublicacion = "2017-01-12", verificacion = VerificacionNorma.TEXTO_PARCIAL,
            observaciones = "Norma Específica de Contabilidad No. 5, modificación 4 (proformas EFE 5920-04 a 5926-04). " +
                "Deroga las resoluciones 1173/2015 y 138/2016. El texto leído no fija fecha de entrada en vigor. " +
                "La Res. 407/2019 modifica las definiciones del Estado de Situación: la cuenta 350 se suma a la 349 en " +
                "la fila 83 y las cuentas 576 a 580 a las 565 a 568 en la fila 123, con desglose en notas. " +
                "Solo se registra como norma relacionada."
        )
        NormativaBase.R166 -> n.copy(
            verificacion = VerificacionNorma.TEXTO_OFICIAL_LEIDO,
            observaciones = "Texto oficial leído (GO Ord. 75, 7/07/2021; firma 11/05/2021). Modifica solo el uso y " +
                "contenido de las cuentas 865-866 en el Anexo 2 de la 494 (y el Anexo de la Res. 497). Se aplica a las " +
                "operaciones \"al cierre de enero de 2021\" (expresión literal; fecha exacta no determinada). " +
                "Derogada completa por la Res. 39/2023, que no dispone qué redacción rige para el Anexo 2 de la " +
                "494: pendiente de interpretación jurídica."
        )
        else -> n
    }

    /** Cuenta 154 y secciones 50.3 y 50.4 del Anexo 1: uso exclusivo de empresas de seguro. */
    val seguros: List<RegistroSeguro> = listOf(
        RegistroSeguro("154", null, "Adeudos con Cobros Diferidos (Uso Exclusivo de Empresas de Seguro)", Naturaleza.DEUDORA, "10", "10.1 ACTIVOS CIRCULANTES"),
        RegistroSeguro("800", null, "Devoluciones de Primas", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("801", null, "Bonificaciones", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("802", null, "Gastos por Cesión de Primas", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("803", "804", "Gastos por Comisiones de Agentes y Corredores", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("810", "811", "Gastos por Indemnizaciones", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("812", null, "Gastos de Reaseguros Aceptados", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("813", "817", "Otros Gastos de la Actividad del Seguro", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("818", null, "Costo por Exportación de Servicios", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("819", "820", "Gastos por Creación de la Provisión Técnica de Riesgos en Curso", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("821", "825", "Gastos por Creación de Otras Provisiones Técnicas del Seguro", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("826", "829", "Gastos de Operación", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("830", "833", "Gastos Generales y de Administración", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("834", null, "Gastos de Administración de la OSDE", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("835", "838", "Gastos Financieros", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("839", null, "Gastos por Pérdidas en Tasas de Cambio", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("840", null, "Financiamiento Entregado a la OSDE", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("843", null, "Gastos por Estadía – Otras Entidades", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("845", "848", "Gastos por Pérdidas", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("849", null, "Gastos por Pérdidas - Desastres", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("850", "854", "Gastos por Faltantes de Bienes", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("855", "864", "Otros Impuestos, Tasas y Contribuciones", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("865", "866", "Otros Gastos", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("867", null, "Gastos de Eventos", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("873", null, "Gastos de Recuperación de Desastres", Naturaleza.DEUDORA, "50", "50.3 CUENTAS NOMINALES DEUDORAS (Empresas de Seguro)"),
        RegistroSeguro("900", "905", "Ingresos de la Actividad de Seguro y Reaseguros", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("906", "909", "Otros Ingresos de la Actividad de Seguro y Reaseguros", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("910", "911", "Recobro a Terceros", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("912", "913", "Liberación de la Provisión de Riesgos en Curso", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("914", null, "Ventas de Bienes con destino a la Exportación", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("915", null, "Ventas por Exportación de Servicios", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("916", "921", "Liberación de Otras Provisiones Técnicas del Seguro", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("922", null, "Ingresos Financieros", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("923", null, "Financiamiento Recibido de las Empresas (Uso exclusivo de la OSDE)", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("924", null, "Ingresos por Variación de Tasas de Cambio", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("925", null, "Ingresos por Dividendos Ganados", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("928", "929", "Ingresos por Recobro de Estadía (importadores y otras entidades)", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("930", "939", "Ingresos por Sobrantes", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("950", "952", "Otros Ingresos", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
        RegistroSeguro("953", null, "Ingresos por Donaciones Recibidas", Naturaleza.ACREEDORA, "50", "50.4 CUENTAS NOMINALES ACREEDORAS (Empresas de Seguro)"),
    )

    fun versionesSeguros(): List<CuentaVersionEntity> = seguros.map {
        CuentaVersionEntity(
            cuentaCodigo = it.codigo, variante = "SEGUROS", denominacion = it.nombre,
            naturaleza = it.naturaleza, grupo = it.grupo, subgrupo = it.subgrupo,
            normaOrigenCodigo = NormativaBase.R494, vigenteDesde = DESDE_494,
            estadoNormativo = EstadoNormativo.CONFIRMADO,
            observaciones = "Uso exclusivo de empresas de seguro; no se carga como cuenta del nomenclador general." +
                (it.hasta?.let { h -> " Rango ${it.codigo}-$h." } ?: "")
        )
    }

    /** Diferencias de tipografía (guion) entre la app y el Anexo 1 oficial. Solo afectan al historial. */
    val correcciones: List<CorreccionDenominacion> = listOf(
        CorreccionDenominacion("182", null, "Ingresos Acumulados por Cobrar - Reaseguros Aceptados", "Ingresos Acumulados por Cobrar – Reaseguros Aceptados"),
        CorreccionDenominacion("364", null, "Fondo de Amortización de Bonos - Efectivo y Valores", "Fondo de Amortización de Bonos – Efectivo y Valores"),
        CorreccionDenominacion("841", null, "Gastos por Estadía - Importadores", "Gastos por Estadía – Importadores"),
        CorreccionDenominacion("843", null, "Gastos por Estadía - Otras Entidades", "Gastos por Estadía – Otras Entidades"),
        CorreccionDenominacion("627", "NO_ESTATAL", "Donaciones Entregadas-Exterior", "Donaciones Entregadas - Exterior")
    )

    // ---------- 865-866: redacción de la Res. 166/2021 ----------
    const val USO_865_166 =
        "Redacción de la Res. 166/2021: gastos ajenos a las actividades fundamentales de la entidad que no son " +
            "gastos financieros, pérdidas ni faltantes. Entre otros: comedores, cafeterías y actividades " +
            "socioculturales, servicios no industriales a trabajadores, capacitación de los trabajadores, " +
            "adquisición de equipos de protección personal, servicios a comunidades y bateyes y mantenimiento " +
            "corriente de activos fijos arrendados; también paralizaciones totales o parciales por causas ajenas " +
            "a la entidad, reparaciones generales en las condiciones fijadas y movilizaciones. Se analizan por los " +
            "elementos de los Sistemas de Costo, diferenciando los gastos de protección del medio ambiente cuando " +
            "sea posible."

    const val OBS_CAMBIO_865 =
        "Rango 865-866 (Otros Gastos). Cambios respecto a la 494: \"empresa\" pasa a \"entidad\"; se elimina la " +
            "exclusión de los gastos de años anteriores; la capacitación deja de limitarse al noveno grado; se añaden " +
            "los equipos de protección personal; se exige diferenciar los gastos de protección del medio ambiente " +
            "cuando sea posible; los saldos se cancelan al cierre del ejercicio económico. Derogada por la Res. " +
            "39/2023 sin disposición expresa sobre el Anexo 2 de la 494: pendiente de interpretación jurídica; no " +
            "se asume que rija la redacción original."

    fun version865Rev166() = CuentaVersionEntity(
        cuentaCodigo = "865", denominacion = "Otros Gastos", naturaleza = Naturaleza.DEUDORA,
        grupo = "50", subgrupo = "50.1", usoContenido = USO_865_166,
        funcionamientoCreditos = "Al cierre del ejercicio económico se cancela su saldo contra la cuenta Resultado.",
        normaOrigenCodigo = NormativaBase.R166, vigenteDesde = DESDE_166_APROX,
        estadoNormativo = EstadoNormativo.PENDIENTE_INTERPRETACION,
        observaciones = "Redacción de la Res. 166/2021, derogada por la Res. 39/2023. La fecha de inicio es aproximada: " +
            "la norma dice \"al cierre de enero de 2021\". Rango 865-866. Pendiente de interpretación jurídica: no se " +
            "sabe si rige esta redacción o la original de la 494."
    )

    // ---------- Fase 1 del uso y contenido (cuentas nominales), en resumen ----------
    private const val CIERRE_D = "Al cierre del ejercicio se cancela su saldo contra la cuenta Resultado."

    val resumenesFase1: Map<String, ResumenUso> = mapOf(
        "800" to ResumenUso(
            "Devoluciones de clientes de productos terminados y mercancías vendidas (por deficiencias, incumplimiento de especificaciones u otras causas aceptadas) ya registradas en Ventas, y rebajas o bonificaciones concedidas sobre las ventas.",
            "Por las devoluciones y por las rebajas o bonificaciones.", CIERRE_D),
        "805" to ResumenUso(
            "Impuestos incluidos en los precios de venta según las disposiciones vigentes; incluye los importes de recuperación aprobados al MINCIN.",
            "Por los impuestos cuya obligación de pago se registra al efectuar las entregas.", CIERRE_D),
        "810" to ResumenUso(
            "Costo de las producciones terminadas, servicios prestados y trabajos ejecutados entregados a los clientes. Se debita a costo real; durante el mes puede usarse costo planificado o precio fijo de registro, con ajuste a costo real a fin de mes.",
            "Por el costo de lo entregado a los clientes.", CIERRE_D),
        "814" to ResumenUso(
            "Costo de las mercancías vendidas y entregadas a los clientes, a costo real (con ajuste de fin de mes si se usa costo planificado). Si las existencias se controlan a precio de venta, el costo puede incluir descuentos comerciales e impuesto de circulación, siempre que se rebajen al cierre de cada mes.",
            "Por el costo de las mercancías vendidas.", CIERRE_D),
        "818" to ResumenUso(
            "Costo de la exportación de servicios, a costo real cuando pueda determinarse (se admite costo planificado durante el mes con ajuste a fin de mes).",
            "Por el costo de los servicios exportados.", CIERRE_D),
        "819" to ResumenUso(
            "Gastos posteriores al proceso productivo para almacenar, entregar y distribuir la producción terminada, asumidos por la entidad y contenidos en el precio de venta. Se analizan por los elementos del Sistema de Costo.",
            "Por los gastos incurridos.", CIERRE_D),
        "822" to ResumenUso(
            "Gastos de administración de la entidad (sin el personal de dirección ligado a la producción) y gastos de carácter general (protección, áreas verdes, etc.): fuerza de trabajo de dirección, comisión de servicios, oficina, depreciación de activos de uso general, cafeterías y locales socioculturales, mantenimiento corriente de instalaciones generales, protección del trabajo y preparación de cuadros. Incluye las pérdidas por paradas improductivas imputables a la organización y dirección. Se analizan por elementos del Sistema de Costo.",
            "Por los gastos incurridos y por las pérdidas por paradas improductivas.", CIERRE_D),
        "825" to ResumenUso(
            "Gastos de actividades de proyectos nacionales, ramales, territoriales e internacionales en las que no hay producción en proceso. Se analizan por los elementos de los lineamientos de costo.",
            "Por los gastos incurridos.", CIERRE_D),
        "826" to ResumenUso(
            "Gastos de actividades sin producción en proceso ni cálculo de costo por producto o servicio (comercio y gastronomía, transporte excepto marítimo, comunicaciones, alojamiento y recreación, entre otras). En comercio y gastronomía no incluye el costo de adquisición de lo vendido, que va a Costo de Ventas.",
            "Por los gastos incurridos.", CIERRE_D),
        "834" to ResumenUso(
            "Gastos de administración de la OSDE o grupo empresarial: fuerza de trabajo de dirección, comisión de servicios, gastos de oficina y depreciación, entre otros. Se analizan por los elementos del Sistema de Costo.",
            "Por los gastos incurridos en el período.", "Al cierre del período contable se cancela su saldo contra la cuenta Resultado."),
        "835" to ResumenUso(
            "Gastos de las operaciones financieras: intereses, comisiones bancarias, multas, sanciones, morosidad e indemnizaciones, otras contribuciones de destino específico, descuentos por pronto pago y pagos por pronto despacho.",
            "Por los gastos financieros incurridos.", CIERRE_D),
        "839" to ResumenUso(
            "Gastos por las pérdidas en las tasas de cambio de las operaciones financieras.",
            "Por las pérdidas en tasa de cambio.", CIERRE_D),
        "840" to ResumenUso(
            "Aporte de la empresa para contribuir al financiamiento de la OSDE. No se analiza por elementos del gasto.",
            "Por el aporte realizado.", CIERRE_D),
        "841" to ResumenUso(
            "Cuenta de las importadoras para los gastos por estadía de buques y otros medios de transporte, demoras en la extracción de mercancías, almacenaje tras los días libres y demoras en la devolución de contenedores, aunque puedan recobrarse. Se analiza por estadía en almacenaje y en devolución de contenedores y, dentro de cada una, por su causa (retención financiera, deudas con operadores portuarios o aéreos, navieras o aduana, errores de documentación del proveedor, conexión de contenedores refrigerados, o gastos imputables a la economía nacional por responsabilidad del comprador interno o del transporte).",
            "Por los gastos de estadía incurridos.", CIERRE_D),
        "843" to ResumenUso(
            "Cuenta del resto de las entidades para los gastos que les facturan los importadores por esos mismos conceptos de estadía. Se analiza igual que la cuenta 841.",
            "Por los gastos de estadía facturados.", CIERRE_D),
        "845" to ResumenUso(
            "Pérdidas ya investigadas que asume la entidad por no poder aplicar responsabilidad material: cosechas agrícolas, valor no depreciado de activos fijos dados de baja antes de terminar su vida útil, sobrevaloración de la masa ganadera, muerte de animales en desarrollo no atribuible a culpables y exceso del costo real sobre el planificado en inversiones con medios propios y en autoconsumo.",
            "Por las pérdidas que asume la entidad.", CIERRE_D),
        "849" to ResumenUso(
            "Pérdidas por desastres (ciclones, intensas lluvias, penetraciones del mar, sequías, incendios de grandes proporciones, sismos, accidentes de sustancias peligrosas, derrames, desastres sanitarios) reconocidos por la autoridad competente y no cubiertas por el seguro.",
            "Por las pérdidas no cubiertas por el seguro.", CIERRE_D),
        "850" to ResumenUso(
            "Faltantes de activos fijos, inventarios, medios monetarios, equipos por instalar y materiales del proceso inversionista, ya investigados y asumidos por la entidad.",
            "Por los faltantes que asume la entidad.", CIERRE_D),
        "855" to ResumenUso(
            "Impuesto sobre el uso de la fuerza de trabajo, contribución a la seguridad social a corto y largo plazo y otros impuestos, tasas y contribuciones. Excluye los impuestos sobre ventas, utilidades e ingresos personales, el impuesto aduanero, la contribución especial y la contribución territorial para el desarrollo local.",
            "Por los importes devengados.", CIERRE_D),
        "867" to ResumenUso(
            "Gastos de preparación y celebración de los eventos nacionales e internacionales aprobados en el Plan de la Economía, registrados por evento. Las reuniones y actividades de la gestión administrativa no son eventos nacionales.",
            "Por los gastos de cada evento.", "Al cierre del ejercicio se cancela su saldo contra la cuenta Resultado, o según disponga la norma contable correspondiente."),
        "873" to ResumenUso(
            "Gastos para reponer y recuperar las condiciones normales de funcionamiento tras afectaciones a los recursos materiales por hechos catastróficos reconocidos por la autoridad competente. Se analiza por elementos del gasto.",
            "Por los gastos de recuperación.", CIERRE_D),
        "900" to ResumenUso(
            "Importes de las entregas a clientes de productos terminados, trabajos efectuados, servicios prestados y mercancías vendidas.",
            CIERRE_D, "Por las ventas realizadas."),
        "915" to ResumenUso(
            "Ingresos por servicios prestados al exterior; se analiza por el Clasificador de Productos de Cuba vigente.",
            CIERRE_D, "Por los servicios exportados."),
        "916" to ResumenUso(
            "Subvenciones que son ingresos a recibir del Presupuesto del Estado por los conceptos aprobados en la legislación. No incluye la subvención por pérdida.",
            CIERRE_D, "Por las subvenciones devengadas."),
        "920" to ResumenUso(
            "Ingresos de las operaciones financieras: intereses ganados, multas e indemnizaciones a terceros, ganancias por cambio de monedas en deudas, pronto despacho, ganancias en negocios conjuntos con entidades extranjeras y recuperación de faltantes de bienes, entre otros.",
            CIERRE_D, "Por los ingresos financieros devengados."),
        "923" to ResumenUso(
            "Cuenta exclusiva de las OSDE para los honorarios de administración o financiamientos recibidos de sus entidades subordinadas para cubrir sus gastos.",
            "Al cierre del ejercicio se cancela su saldo contra la cuenta Resultado.",
            "Por la recepción del financiamiento o el traslado de ingresos diferidos."),
        "924" to ResumenUso(
            "Ganancia por la fluctuación de la tasa de cambio de las monedas extranjeras.",
            CIERRE_D, "Por las ganancias en tasa de cambio."),
        "925" to ResumenUso(
            "Ingresos de operaciones financieras derivados de dividendos.",
            CIERRE_D, "Por los dividendos ganados."),
        "926" to ResumenUso(
            "Ingresos devengados por las navieras y operadores por la estadía facturada a los importadores.",
            "Al cierre del año se cancela su saldo contra la cuenta Resultado.", "Por los ingresos recibidos."),
        "928" to ResumenUso(
            "Ingresos devengados por los importadores al facturar a otras entidades nacionales o a proveedores extranjeros las estadías pagadas a la naviera u operador, cuando la causa no es imputable al importador.",
            "Al cierre del año se cancela su saldo contra la cuenta Resultado.", "Por los ingresos recibidos."),
        "930" to ResumenUso(
            "Sobrantes de inventarios y medios monetarios, ya investigados y asumidos por la entidad, y sobrantes de animales en desarrollo.",
            CIERRE_D, "Por los sobrantes que asume la entidad."),
        "950" to ResumenUso(
            "Ingresos ajenos a las actividades fundamentales de la entidad que no son ingresos financieros: ventas de alimentos en comedores y cafeterías, venta de materias primas y materiales recuperados no deducibles del costo, servicios y ventas a trabajadores, arrendamiento de activos fijos tangibles y servicios a comunidades y bateyes en correspondencia con los gastos asumidos.",
            CIERRE_D, "Por los ingresos devengados."),
        "953" to ResumenUso(
            "Recursos materiales y monetarios recibidos de entidades, personas, gobiernos u organizaciones nacionales o extranjeras sin obligación de pago, incluidos los que provienen de decomisos para uso de la entidad. Excluye los recursos monetarios destinados a financiar la adquisición de activos fijos tangibles, que van a las donaciones recibidas del patrimonio.",
            CIERRE_D, "Por la recepción de los recursos."),
        "999" to ResumenUso(
            "Refleja al cierre el resultado (utilidad o pérdida) obtenido al cerrar los saldos de las cuentas nominales. Su saldo se traslada a Utilidades Retenidas si es positivo y a Pérdida si es negativo.",
            null, null)
    )
}
