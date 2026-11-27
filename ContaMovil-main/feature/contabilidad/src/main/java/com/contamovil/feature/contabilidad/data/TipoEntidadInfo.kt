package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.entity.TipoEntidad

fun TipoEntidad.nombreVisible(): String = when (this) {
    TipoEntidad.MIPYME_PRIVADA -> "MIPYME privada"
    TipoEntidad.MIPYME_ESTATAL -> "MIPYME estatal"
    TipoEntidad.TRABAJADOR_CUENTA_PROPIA -> "Trabajador por cuenta propia"
    TipoEntidad.OTRA_ESTATAL -> "Otra entidad estatal"
    TipoEntidad.OTRA_NO_ESTATAL -> "Otra entidad no estatal (cooperativa, sociedad mercantil…)"
}

/** Variante del grupo 30 (patrimonio) del nomenclador que corresponde a este tipo de entidad. */
fun TipoEntidad.varianteNomenclador(): VarianteNomenclador = when (this) {
    TipoEntidad.MIPYME_ESTATAL, TipoEntidad.OTRA_ESTATAL -> VarianteNomenclador.ESTATAL
    TipoEntidad.MIPYME_PRIVADA,
    TipoEntidad.TRABAJADOR_CUENTA_PROPIA,
    TipoEntidad.OTRA_NO_ESTATAL -> VarianteNomenclador.NO_ESTATAL
}

fun TipoEntidad.textoVariante(): String =
    if (varianteNomenclador() == VarianteNomenclador.ESTATAL) "estatal" else "no estatal"

/** Normas relacionadas con este tipo de entidad (resumen; el detalle está en la pantalla de Normativa). */
fun TipoEntidad.normasAplicables(): List<String> = when (this) {
    TipoEntidad.MIPYME_PRIVADA, TipoEntidad.MIPYME_ESTATAL -> listOf(
        "Res. 494/2016 y modificaciones: nomenclador de cuentas.",
        "Res. 351/2021: las MIPYMES aplican las Normas Cubanas de Información Financiera.",
        "Res. 306/2023: regulaciones tributarias, financieras y de precios."
    )
    TipoEntidad.TRABAJADOR_CUENTA_PROPIA -> listOf(
        "Res. 494/2016 y modificaciones: nomenclador de cuentas.",
        "Res. 272/2024: contabilidad de los trabajadores por cuenta propia."
    )
    TipoEntidad.OTRA_ESTATAL, TipoEntidad.OTRA_NO_ESTATAL -> listOf(
        "Res. 494/2016 y modificaciones: nomenclador de cuentas."
    )
}

/** Aclaraciones y límites de lo verificado para cada tipo de entidad. */
fun TipoEntidad.nota(): String? = when (this) {
    TipoEntidad.MIPYME_PRIVADA ->
        "Los aportes de los socios van en el grupo 30 (variante no estatal). Una fuente secundaria indica " +
            "que la Res. 351/2021 autoriza usar la cuenta 600; no está verificado en el texto oficial."
    TipoEntidad.MIPYME_ESTATAL ->
        "Se asigna la variante estatal del patrimonio por ser entidad estatal. No verifiqué si existen " +
            "adecuaciones específicas para las MIPYMES estatales."
    TipoEntidad.TRABAJADOR_CUENTA_PROPIA ->
        "La Res. 272/2024 obliga a llevar contabilidad a quienes superan 500 000 CUP de ingresos anuales; " +
            "para ingresos menores no verifiqué qué aplica. La variante no estatal del patrimonio es una " +
            "asignación propuesta, no confirmada por la norma."
    TipoEntidad.OTRA_ESTATAL, TipoEntidad.OTRA_NO_ESTATAL -> null
}
