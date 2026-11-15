package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.entity.CuentaEntity

/**
 * Propuesta de ubicación para una cuenta nueva.
 *
 * @property madre cuenta madre propuesta (null = cuenta principal).
 * @property referencia cuenta del nomenclador cuyo rango contiene el código (ej. 101 para 102).
 * @property fueraDeRango el código es numérico y no cae en ningún rango del nomenclador.
 * @property madreFaltante código de la madre que se esperaba pero no existe (códigos con punto).
 */
data class Sugerencia(
    val madre: CuentaEntity? = null,
    val referencia: CuentaEntity? = null,
    val fueraDeRango: Boolean = false,
    val madreFaltante: String? = null
)

/** Deduce dónde colocar una cuenta nueva a partir de su código. */
fun sugerirMadre(codigo: String, cuentas: List<CuentaEntity>): Sugerencia {
    val cod = codigo.trim()
    if (cod.isEmpty() || cod.endsWith('.')) return Sugerencia()
    if (cuentas.any { it.codigo == cod }) return Sugerencia() // duplicado: se avisa al guardar

    // Subcuenta: 135.0070 va dentro de 135
    if (cod.contains('.')) {
        val codPadre = cod.substringBeforeLast('.')
        val padre = cuentas.firstOrNull { it.codigo == codPadre }
        return Sugerencia(
            madre = padre,
            madreFaltante = if (padre == null) codPadre else null
        )
    }

    // Código numérico: buscar el rango del nomenclador que lo contiene (102 está en 101-108)
    if (cod.all { it.isDigit() }) {
        val n = cod.toLongOrNull() ?: return Sugerencia()
        val referencia = cuentas.firstOrNull { c ->
            if (c.codigo.contains('.')) {
                false
            } else {
                val desde = c.codigo.toLongOrNull()
                val hasta = c.codigoHasta?.toLongOrNull() ?: desde
                desde != null && hasta != null && n in desde..hasta
            }
        }
        if (referencia != null) {
            val madre = referencia.cuentaPadreId?.let { id -> cuentas.firstOrNull { it.id == id } }
            return Sugerencia(madre = madre, referencia = referencia)
        }
        // Solo se avisa de "fuera de rango" si el nomenclador ya está cargado
        return Sugerencia(fueraDeRango = cuentas.any { it.codigoHasta != null })
    }
    return Sugerencia()
}
