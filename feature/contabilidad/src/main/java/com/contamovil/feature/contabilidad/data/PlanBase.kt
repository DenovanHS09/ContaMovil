package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.entity.Naturaleza
import com.contamovil.core.database.entity.Naturaleza.ACREEDORA
import com.contamovil.core.database.entity.Naturaleza.DEUDORA
import com.contamovil.core.database.entity.Naturaleza.MIXTA
import com.contamovil.core.database.entity.TipoCuenta

/**
 * Nomenclador de Cuentas para la actividad empresarial, unidades presupuestadas de
 * tratamiento especial y sector cooperativo (Resolución 494/2016 del MFP, Anexo 1).
 *
 * Estructura: grupo (10) > subgrupo (10.1) > cuenta (101) > subcuenta obligatoria (135.0010).
 * Las cuentas definidas por rango en la norma ("101 a 108") se cargan con su primer código
 * y el rango queda indicado entre corchetes en el nombre: el usuario puede abrir los demás
 * códigos del rango. Se omiten las cuentas de uso exclusivo de empresas de seguro.
 *
 * IMPORTANTE: verificar siempre contra la versión vigente publicada por el MFP.
 */
enum class VarianteNomenclador { ESTATAL, NO_ESTATAL }

data class CuentaBase(
    val codigo: String,
    val nombre: String,
    val tipo: TipoCuenta,
    val naturaleza: Naturaleza,
    val padre: String? = null
)

private class Sub(val codigo: String, val nombre: String, val nat: Naturaleza? = null)

private val S6 = listOf(
    Sub("0010", "Dentro del Órgano u Organismo"),
    Sub("0020", "Fuera del Órgano u Organismo"),
    Sub("0030", "En el Extranjero"),
    Sub("0040", "Dentro del Grupo Empresarial"),
    Sub("0050", "Sector Cooperativo"),
    Sub("0060", "Personas Naturales")
)
private val S4 = S6.take(4)

private val SUB_PRESUPUESTO = listOf(
    Sub("0001", "Impuesto sobre Ventas"),
    Sub("0002", "Impuesto sobre los Servicios Públicos"),
    Sub("0003", "Aranceles de Aduana"),
    Sub("0004", "Impuesto sobre Utilidades"),
    Sub("0005", "Impuesto sobre Ingresos Personales"),
    Sub("0006", "Impuesto sobre los Recursos"),
    Sub("0007", "Otros Impuestos"),
    Sub("0008", "Contribuciones"),
    Sub("0009", "Tasas"),
    Sub("0010", "Ingresos No tributarios"),
    Sub("0011", "Rentas de la Propiedad"),
    Sub("0012", "Transferencias Corrientes"),
    Sub("0013", "Ingresos de Operaciones"),
    Sub("0014", "Recursos Propios de Capital"),
    Sub("0015", "Transferencias de Capital"),
    Sub("0016", "Otros Recursos Financieros")
)

private val SUB_DONACIONES = listOf(
    Sub("0010", "Activos Fijos Tangibles"),
    Sub("0020", "Activos Fijos Intangibles"),
    Sub("0030", "Medios Monetarios"),
    Sub("0040", "Insumos"),
    Sub("0050", "Otros")
)

private val SUB_COMPRA_AF = listOf(
    Sub("0100", "Compras del Período"),
    Sub("0300", "Adquisición del Período por Donaciones")
)

private val SUB_DIFERIDOS = listOf(
    Sub("0010", "Saldo al Inicio del Año"),
    Sub("0020", "Gastos del Período"),
    Sub("0030", "Amortización de Gastos", ACREEDORA)
)

private class Constructor {
    val lista = mutableListOf<CuentaBase>()
    private var tipo = TipoCuenta.ACTIVO
    private var grupoActual = ""
    private var subgrupoActual = ""

    fun grupo(cod: String, nombre: String, t: TipoCuenta) {
        tipo = t; grupoActual = cod; subgrupoActual = cod
        lista += CuentaBase(cod, nombre, t, t.naturaleza)
    }

    fun subgrupo(cod: String, nombre: String, t: TipoCuenta = tipo) {
        tipo = t; subgrupoActual = cod
        lista += CuentaBase(cod, nombre, t, t.naturaleza, grupoActual)
    }

    /** Cuenta (con rango opcional) y sus subcuentas obligatorias. */
    fun c(
        cod: String, hasta: String?, nombre: String, nat: Naturaleza,
        subs: List<Sub> = emptyList()
    ) {
        val nom = if (hasta != null) "$nombre [$cod-$hasta]" else nombre
        lista += CuentaBase(cod, nom, tipo, nat, subgrupoActual)
        subs.forEach {
            lista += CuentaBase("$cod.${it.codigo}", it.nombre, tipo, it.nat ?: nat, cod)
        }
    }
}

private fun Constructor.activos() {
    grupo("10", "ACTIVOS", TipoCuenta.ACTIVO)

    subgrupo("10.1", "ACTIVOS CIRCULANTES")
    c("101", "108", "Efectivo en Caja", DEUDORA)
    c("109", "119", "Efectivo en Banco y en Otras Instituciones", DEUDORA)
    c("120", "129", "Inversiones a Corto Plazo o Temporales", DEUDORA)
    c("130", "133", "Efectos por Cobrar a Corto Plazo", DEUDORA, S6)
    c("134", null, "Cuenta en Participación", DEUDORA)
    c("135", "139", "Cuentas por Cobrar a Corto Plazo", DEUDORA, S6)
    c("140", null, "Pagos por Cuenta de Terceros", DEUDORA)
    c("141", null, "Participación de Reaseguradores por Siniestros Pendientes", DEUDORA)
    c("142", null, "Préstamos y Otras Operaciones Crediticias a Cobrar a Corto Plazo", DEUDORA)
    c("143", null, "Suscriptores de Bonos", DEUDORA)
    c("144", null, "Bonos Emitidos en Cartera", DEUDORA)
    c("146", "149", "Pagos Anticipados a Suministradores", DEUDORA, S6)
    c("150", "152", "Pagos Anticipados del Proceso Inversionista", DEUDORA, S6)
    c("153", null, "Materiales Anticipados del Proceso Inversionista", DEUDORA)
    c("161", "163", "Anticipos a Justificar", DEUDORA)
    c(
        "164", "166", "Adeudos del Presupuesto del Estado", DEUDORA,
        SUB_PRESUPUESTO + listOf(
            Sub("0020", "Invalidez Parcial"),
            Sub("0030", "Licencias de Maternidad"),
            Sub("0040", "Reintegro Aporte de Microbrigada"),
            Sub("0090", "Otros")
        )
    )
    c("167", "170", "Adeudos del Órgano u Organismo", DEUDORA)
    c("173", "180", "Ingresos Acumulados por Cobrar", DEUDORA)
    c("181", null, "Dividendos y Participaciones por Cobrar", DEUDORA)
    c("182", null, "Ingresos Acumulados por Cobrar - Reaseguros Aceptados", DEUDORA)
    c("183", null, "Materias Primas y Materiales", DEUDORA)
    c("184", null, "Combustibles y Lubricantes", DEUDORA)
    c("185", null, "Partes y Piezas de Repuesto", DEUDORA)
    c("186", null, "Envases y Embalajes", DEUDORA)
    c("187", null, "Útiles, Herramientas y Otros", DEUDORA)
    c(
        "188", null, "Producción Terminada", DEUDORA,
        listOf(
            Sub("0020", "Producción y Ventas"),
            Sub("0040", "Otros Aumentos"),
            Sub("0050", "Otras Disminuciones", ACREEDORA),
            Sub("0099", "Contrapartida (Opcional)", MIXTA)
        )
    )
    c("189", null, "Mercancías para la Venta", DEUDORA)
    c("190", null, "Medicamentos", DEUDORA)
    c("191", null, "Base Material de Estudio", DEUDORA)
    c("192", null, "Vestuario y Lencería", DEUDORA)
    c("193", null, "Alimentos", DEUDORA)
    c("194", null, "Inventarios de Mercancías de Importación", DEUDORA)
    c("195", null, "Inventarios de Mercancías de Exportación", DEUDORA)
    c(
        "196", null, "Producción para Insumo o Autoconsumo", DEUDORA,
        listOf(
            Sub("0020", "Producción"),
            Sub("0030", "Traspaso a Producciones en Proceso", ACREEDORA),
            Sub("0040", "Otros Aumentos"),
            Sub("0050", "Otras Disminuciones", ACREEDORA),
            Sub("0099", "Contrapartida (Opcional)", MIXTA)
        )
    )
    c("205", "207", "Otros Inventarios", DEUDORA)
    c("208", null, "Inventarios Ociosos", DEUDORA)
    c("209", null, "Inventarios de Lento Movimiento", DEUDORA)
    c("211", null, "Créditos Documentarios", DEUDORA)

    subgrupo("10.2", "ACTIVO A LARGO PLAZO")
    c("215", "217", "Efectos por Cobrar a Largo Plazo", DEUDORA, S6)
    c("218", "220", "Cuentas por Cobrar a Largo Plazo", DEUDORA, S6)
    c("221", "224", "Préstamos Concedidos a Cobrar a Largo Plazo", DEUDORA, S4)
    c("225", "234", "Inversiones a Largo Plazo o Permanentes", DEUDORA)

    subgrupo("10.3", "ACTIVOS FIJOS")
    c("240", "251", "Activos Fijos Tangibles", DEUDORA)
    c("252", null, "Fondos Bibliotecarios", DEUDORA)
    c("253", null, "Medios y Equipos para Alquilar", DEUDORA)
    c("254", null, "Monumentos y Obras de Arte", DEUDORA)
    c("255", "263", "Activos Fijos Intangibles", DEUDORA)
    c(
        "264", null, "Activos Fijos Intangibles en Proceso", DEUDORA,
        listOf(
            Sub("0001", "Saldo al Inicio del Año"),
            Sub("0010", "Operaciones del Año"),
            Sub("0998", "Traspaso a Gastos por Faltantes y Pérdidas Diferidos", ACREEDORA),
            Sub("0999", "Traspaso a Activos Fijos Intangibles", ACREEDORA)
        )
    )
    c(
        "265", "278", "Inversiones en Proceso", DEUDORA,
        listOf(
            Sub("0001", "Saldo al Inicio del Año"),
            Sub("0010", "Construcción y Montaje"),
            Sub("0020", "Equipos"),
            Sub("0030", "Otros Gastos"),
            Sub("0050", "Fomentos Agrícolas"),
            Sub("0060", "Fomentos y Desarrollo Mineros"),
            Sub("0070", "Fomentos y Desarrollo Forestales"),
            Sub("0100", "Otros no Especificados"),
            Sub("0200", "Fondo de Fomento Desarrollo Ganadero"),
            Sub("0999", "Traspaso a Activos Fijos Tangibles", ACREEDORA)
        )
    )
    c(
        "279", null, "Plan de Preparación de Inversiones", DEUDORA,
        listOf(
            Sub("0001", "Saldo al Inicio del Año"),
            Sub("0010", "Construcción y Montaje"),
            Sub("0020", "Equipos"),
            Sub("0030", "Otros"),
            Sub("0998", "Traspaso a Gastos del Proceso Inversionista", ACREEDORA)
        )
    )
    c("280", "289", "Equipos por Instalar y Materiales para el Proceso Inversionista", DEUDORA)
    c(
        "290", null, "Adquisición de Activos Fijos Tangibles Nuevos", DEUDORA,
        SUB_COMPRA_AF + Sub("0999", "Traspaso a Activos Fijos Tangibles", ACREEDORA)
    )
    c(
        "291", null, "Compra de Activos Fijos Tangibles de Uso", DEUDORA,
        SUB_COMPRA_AF + Sub("0999", "Traspaso a Activos Fijos Tangibles", ACREEDORA)
    )
    c(
        "292", null, "Compra de Activos Fijos Intangibles", DEUDORA,
        SUB_COMPRA_AF + Sub("0999", "Traspaso a Activos Fijos Intangibles", ACREEDORA)
    )

    subgrupo("10.4", "ACTIVOS DIFERIDOS")
    c("300", "305", "Gastos de Producción y Servicios Diferidos", DEUDORA,
        listOf(
            Sub("0010", "Saldo al Inicio del Año"),
            Sub("0020", "Gastos por Elementos del Período"),
            Sub("0030", "Amortización de Gastos", ACREEDORA)
        )
    )
    c("306", "307", "Gastos Financieros Diferidos", DEUDORA, SUB_DIFERIDOS)
    c("310", "311", "Gastos del Proceso Inversionista Diferidos", DEUDORA, SUB_DIFERIDOS)
    c(
        "312", null, "Gastos por Faltantes y Pérdidas Diferidos", DEUDORA,
        listOf(
            Sub("0010", "Saldo al Inicio del Año"),
            Sub("0020", "Gastos del Período"),
            Sub("0030", "Amortización de Gastos a las Cuentas de Gastos por Faltantes y Pérdidas", ACREEDORA)
        )
    )

    subgrupo("10.5", "OTROS ACTIVOS")
    c(
        "330", "331", "Pérdidas en Investigación", DEUDORA,
        listOf(
            Sub("0010", "Pérdidas por Deterioro"),
            Sub("0020", "Pérdidas Cosecha"),
            Sub("0030", "Pérdidas Cuentas por Cobrar"),
            Sub("0040", "Pérdidas por Siniestros"),
            Sub("0050", "Otros")
        )
    )
    c(
        "332", "333", "Faltantes de Bienes en Investigación", DEUDORA,
        listOf(
            Sub("0010", "Medios Monetarios"),
            Sub("0020", "Medios Materiales"),
            Sub("0030", "Activos Fijos"),
            Sub("0040", "Equipos por Instalar y Materiales de Construcción"),
            Sub("0050", "Otros")
        )
    )
    c(
        "334", "341", "Cuentas por Cobrar Diversas - Operaciones Corrientes", DEUDORA,
        listOf(
            Sub("0010", "Ventas a Entidades"),
            Sub("0020", "Deudas a Trabajadores"),
            Sub("0030", "Reclamaciones"),
            Sub("0040", "Responsabilidad Material"),
            Sub("0050", "Sector Cooperativo"),
            Sub("0060", "Personas Naturales"),
            Sub("0099", "Otros")
        )
    )
    c("342", null, "Cuentas por Cobrar - Compra de Monedas", DEUDORA)
    c("343", "345", "Cuentas por Cobrar del Proceso Inversionista", DEUDORA, S6)
    c("346", null, "Efectos por Cobrar en Litigio", DEUDORA, S6)
    c("347", null, "Cuentas por Cobrar en Litigio", DEUDORA, S6)
    c("348", null, "Efectos por Cobrar Protestados", DEUDORA, S6)
    c("349", null, "Cuentas por Cobrar en Proceso Judicial", DEUDORA, S6)
    c("354", "355", "Depósitos y Fianzas", DEUDORA)
    c("363", null, "Bonos Redimidos", DEUDORA)
    c("364", null, "Fondo de Amortización de Bonos - Efectivo y Valores", DEUDORA)

    subgrupo("10.6", "CUENTAS REGULADORAS DE ACTIVOS")
    c("365", null, "Efectos por Cobrar Descontados", ACREEDORA)
    c("366", null, "Desgaste de Base Material de Estudio", ACREEDORA)
    c("367", null, "Desgaste de Vestuario y Lencería", ACREEDORA)
    c("369", null, "Provisión para Cuentas Incobrables", ACREEDORA)
    c("370", "372", "Descuento Comercial e Impuesto", ACREEDORA)
    c("373", null, "Desgaste de Útiles y Herramientas", ACREEDORA)
    c("374", null, "Otras Provisiones Reguladoras de Activos", ACREEDORA)
    c("375", "388", "Depreciación de Activos Fijos Tangibles", ACREEDORA)
    c("389", null, "Desgaste de Medios y Equipos para Alquilar", ACREEDORA)
    c("390", "399", "Amortización de Activos Fijos Intangibles", ACREEDORA)
}

private fun Constructor.pasivos() {
    grupo("20", "PASIVOS", TipoCuenta.PASIVO)

    subgrupo("20.1", "PASIVOS CIRCULANTES")
    c("400", null, "Sobregiro Bancario", ACREEDORA)
    c("401", "404", "Efectos por Pagar a Corto Plazo", ACREEDORA, S6)
    c("405", "415", "Cuentas por Pagar a Corto Plazo", ACREEDORA, S6)
    c("416", null, "Cobros por Cuenta de Terceros", ACREEDORA)
    c("417", null, "Dividendos y Participaciones por Pagar", ACREEDORA)
    c("418", "420", "Cuentas en Participación", ACREEDORA)
    c("421", "424", "Cuentas por Pagar - Activos Fijos Tangibles", ACREEDORA, S6)
    c("425", "429", "Cuentas por Pagar del Proceso Inversionista", ACREEDORA, S6)
    c("430", "433", "Cobros Anticipados", ACREEDORA)
    c("434", null, "Materiales Recibidos de Forma Anticipada", ACREEDORA)
    c("435", "439", "Depósitos Recibidos", ACREEDORA)
    c("440", "449", "Obligaciones con el Presupuesto del Estado", ACREEDORA, SUB_PRESUPUESTO)
    c("450", "453", "Obligaciones con el Órgano u Organismo", ACREEDORA)
    c("455", "459", "Nóminas por Pagar", ACREEDORA)
    c("460", "469", "Retenciones por Pagar", ACREEDORA)
    c("470", "479", "Préstamos Recibidos y Otras Operaciones Crediticias por Pagar", ACREEDORA)
    c("480", "489", "Gastos Acumulados por Pagar", ACREEDORA)
    c("492", null, "Provisión para Vacaciones", ACREEDORA)
    c("493", "499", "Otras Provisiones Operacionales", ACREEDORA)
    c("500", null, "Provisión para Pagos de los Subsidios de Seguridad Social a Corto Plazo", ACREEDORA)
    c("501", null, "Fondo de Compensación para Desbalances Financieros (uso exclusivo de la OSDE)", ACREEDORA)

    subgrupo("20.2", "PASIVOS A LARGO PLAZO")
    c("510", "514", "Efectos por Pagar a Largo Plazo", ACREEDORA, S6)
    c("515", "518", "Cuentas por Pagar a Largo Plazo", ACREEDORA, S6)
    c("520", "524", "Préstamos Recibidos por Pagar a Largo Plazo", ACREEDORA)
    c("525", "532", "Obligaciones a Largo Plazo", ACREEDORA, S6)
    c("533", "539", "Otras Provisiones a Largo Plazo", ACREEDORA)
    c("540", null, "Bonos por Pagar", ACREEDORA)
    c("541", null, "Bonos Suscritos", ACREEDORA)

    subgrupo("20.3", "PASIVOS DIFERIDOS")
    c("545", "548", "Ingresos Diferidos", ACREEDORA)
    c("549", null, "Ingresos Diferidos por Donaciones Recibidas", ACREEDORA)

    subgrupo("20.4", "OTROS PASIVOS")
    c("555", "564", "Sobrantes en Investigación", ACREEDORA)
    c(
        "565", "568", "Cuentas por Pagar Diversas", ACREEDORA,
        S6 + listOf(Sub("0070", "A trabajadores"), Sub("0999", "Otros"))
    )
    c("569", null, "Cuentas por Pagar - Compra de Monedas", ACREEDORA)
    c("570", "574", "Ingresos de Períodos Futuros", ACREEDORA)
    c("575", null, "Obligación con el Presupuesto del Estado por Garantía Activada", ACREEDORA)
}

private fun Constructor.patrimonio(v: VarianteNomenclador) {
    if (v == VarianteNomenclador.ESTATAL) {
        grupo("30", "PATRIMONIO NETO (Entidades Estatales)", TipoCuenta.PATRIMONIO)
        c("600", "612", "Inversión Estatal", ACREEDORA)
        c("613", "615", "Revalorización de Activos Fijos Tangibles", MIXTA)
        c("617", "618", "Recursos Recibidos", ACREEDORA)
        c("619", null, "Recursos Entregados", DEUDORA, listOf(Sub("0010", "Minoración de Fondos")))
        c("620", null, "Donaciones Recibidas-Nacionales", ACREEDORA, SUB_DONACIONES)
        c("621", null, "Donaciones Recibidas-Exterior", ACREEDORA, SUB_DONACIONES)
        c("626", null, "Donaciones Entregadas-Nacionales", DEUDORA, SUB_DONACIONES)
        c("627", null, "Donaciones Entregadas-Exterior", DEUDORA, SUB_DONACIONES)
        c("630", "634", "Utilidades Retenidas", ACREEDORA)
        c("635", "639", "Subvención por Pérdida", ACREEDORA)
        c("640", "644", "Pérdida", DEUDORA)
        c("645", null, "Reservas para Contingencias", ACREEDORA)
        c("646", "654", "Otras Reservas Patrimoniales", ACREEDORA)
        c("688", null, "Fondo de Contravalor para Proyectos de Inversión", ACREEDORA)
        c("690", null, "Pago a Cuenta de las Utilidades", DEUDORA)
        c("691", null, "Pago a Cuenta de los Dividendos", DEUDORA)
        c("696", null, "Operaciones entre Dependencias", MIXTA)
        c("697", null, "Revaluación de Inventarios", MIXTA)
        c("698", null, "Ganancia o Pérdida no Realizada", MIXTA)
        c("699", null, "Transitoria del Sistema Automatizado", MIXTA)
    } else {
        grupo(
            "30", "CAPITAL CONTABLE (Entidades no Estatales, Sociedades Mercantiles, Cooperativas y Sector Privado)",
            TipoCuenta.PATRIMONIO
        )
        c("600", null, "Patrimonio y Fondo Común", ACREEDORA)
        c("601", "603", "Capital Social Autorizado", ACREEDORA)
        c("604", "606", "Acciones por Emitir", DEUDORA)
        c("607", "609", "Acciones Suscritas", ACREEDORA)
        c("610", null, "Subscriptores de Acciones", DEUDORA)
        c("611", "612", "Acciones en Tesorería", DEUDORA)
        c("613", "615", "Revalorización de Activos Fijos Tangibles", MIXTA)
        c("616", "619", "Otras Operaciones de Capital", MIXTA)
        c("620", null, "Donaciones Recibidas-Nacionales", ACREEDORA)
        c("621", null, "Donaciones Recibidas-Exterior", ACREEDORA)
        c("626", null, "Donaciones Entregadas-Nacionales", DEUDORA)
        c("627", null, "Donaciones Entregadas-Exterior", DEUDORA)
        c("630", "634", "Utilidades Retenidas", ACREEDORA)
        c("635", "639", "Subvención por Pérdida", ACREEDORA)
        c("640", "644", "Pérdida", DEUDORA)
        c("645", null, "Reservas para Contingencias", ACREEDORA)
        c("646", "654", "Otras Reservas Patrimoniales", ACREEDORA)
        c("688", null, "Fondo de Contravalor para Proyectos de Inversión", ACREEDORA)
        c("690", null, "Pago a Cuentas de las Utilidades Sociedades Mercantiles", DEUDORA)
        c("691", null, "Pago a Cuenta de los Dividendos", DEUDORA)
        c(
            "693", null, "Pago a Cuenta de Utilidades Sector Cooperativo", DEUDORA,
            listOf(
                Sub("0010", "Anticipo a Cooperativistas"),
                Sub("0020", "Pago de Tributos"),
                Sub("0030", "Otros")
            )
        )
        c("696", null, "Operaciones entre Dependencias (Solo para el Sector Cooperativo)", MIXTA)
        c("697", null, "Revaluación de Inventarios", MIXTA)
        c("698", null, "Ganancia o Pérdida no Realizada", MIXTA)
        c("699", null, "Transitoria del Sistema Automatizado", MIXTA)
    }
}

private fun Constructor.produccion() {
    grupo("40", "GASTOS DE PRODUCCIÓN", TipoCuenta.PRODUCCION)
    val saldo = Sub("0010", "Saldo al Inicio del Año")
    val gastos = Sub("0020", "Gastos del Período")
    c(
        "700", "724", "Producción en Proceso", DEUDORA,
        listOf(
            saldo, gastos,
            Sub("0030", "Aumento"),
            Sub("0040", "Disminución", ACREEDORA),
            Sub("0050", "Traspaso a Producción Terminada", ACREEDORA)
        )
    )
    c(
        "725", null, "Producción Propia para Insumos", DEUDORA,
        listOf(
            saldo, gastos,
            Sub("0030", "Aumento"),
            Sub("0040", "Disminución", ACREEDORA),
            Sub("0050", "Traspaso a Producción en Proceso", ACREEDORA),
            Sub("0060", "Traspaso a Inventario de Producción Propia para insumo", ACREEDORA)
        )
    )
    val traspasoInv = Sub("0050", "Traspaso a Inversiones en Proceso", ACREEDORA)
    c("726", null, "Reparaciones Capitales con Medios Propios", DEUDORA, listOf(saldo, gastos, traspasoInv))
    c("727", null, "Inversiones con Medios Propios Activos Fijos Intangibles", DEUDORA, listOf(saldo, gastos, traspasoInv))
    c("728", null, "Inversiones con Medios Propios", DEUDORA, listOf(saldo, gastos, traspasoInv))
    c("731", "739", "Gastos Asociados a la Producción", DEUDORA)
}

private fun Constructor.nominales() {
    grupo("50", "CUENTAS NOMINALES", TipoCuenta.GASTO)

    subgrupo("50.1", "CUENTAS NOMINALES DEUDORAS", TipoCuenta.GASTO)
    c("800", "804", "Devoluciones y Rebajas en Ventas", DEUDORA)
    c("805", "809", "Impuesto por las Ventas", DEUDORA)
    c("810", "813", "Costo de Ventas de la Producción", DEUDORA)
    c("814", "817", "Costo de Ventas de Mercancías", DEUDORA)
    c("818", null, "Costo por Exportación de Servicios", DEUDORA)
    c("819", "821", "Gastos de Distribución y Ventas", DEUDORA)
    c("822", "824", "Gastos Generales y de Administración", DEUDORA)
    c("825", null, "Gastos de Proyectos", DEUDORA)
    c("826", "833", "Gastos de Operación", DEUDORA)
    c("834", null, "Gastos de Administración de la OSDE", DEUDORA)
    c("835", "838", "Gastos Financieros", DEUDORA)
    c("839", null, "Gastos por Pérdidas en Tasas de Cambio", DEUDORA)
    c("840", null, "Financiamiento Entregado a la OSDE", DEUDORA)
    c("841", null, "Gastos por Estadía - Importadores", DEUDORA)
    c("843", null, "Gastos por Estadía - Otras Entidades", DEUDORA)
    c("845", "848", "Gastos por Pérdidas", DEUDORA)
    c("849", null, "Gastos por Pérdidas - Desastres", DEUDORA)
    c("850", "854", "Gastos por Faltantes de Bienes", DEUDORA)
    c("855", "864", "Otros Impuestos, Tasas y Contribuciones", DEUDORA)
    c("865", "866", "Otros Gastos", DEUDORA)
    c("867", null, "Gastos de Eventos", DEUDORA)
    c("873", null, "Gastos de Recuperación de Desastres", DEUDORA)

    subgrupo("50.2", "CUENTAS NOMINALES ACREEDORAS", TipoCuenta.INGRESO)
    c("900", "913", "Ventas", ACREEDORA)
    c("914", null, "Ventas de Bienes con destino a la Exportación", ACREEDORA)
    c("915", null, "Ventas por Exportación de Servicios", ACREEDORA)
    c("916", "919", "Subvenciones", ACREEDORA)
    c("920", "922", "Ingresos Financieros", ACREEDORA)
    c("923", null, "Financiamiento Recibido de las Empresas (Uso exclusivo de la OSDE)", ACREEDORA)
    c("924", null, "Ingresos por Variación de Tasas de Cambio", ACREEDORA)
    c("925", null, "Ingresos por Dividendos Ganados", ACREEDORA)
    c("926", "927", "Ingresos por Estadía (navieras y operadores)", ACREEDORA)
    c("928", "929", "Ingresos por Recobro de Estadía (importadores y otras entidades)", ACREEDORA)
    c("930", "939", "Ingresos por Sobrantes", ACREEDORA)
    c("950", "952", "Otros Ingresos", ACREEDORA)
    c("953", null, "Ingresos por Donaciones Recibidas", ACREEDORA)
}

private fun Constructor.cierre() {
    grupo("60", "CUENTA DE CIERRE", TipoCuenta.CIERRE)
    subgrupo("60.1", "CIERRE")
    c("999", null, "Resultado", MIXTA)
}

object PlanBase {
    fun cuentas(variante: VarianteNomenclador): List<CuentaBase> =
        Constructor().apply {
            activos()
            pasivos()
            patrimonio(variante)
            produccion()
            nominales()
            cierre()
        }.lista
}
