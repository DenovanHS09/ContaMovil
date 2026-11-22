package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.CuentaDao
import javax.inject.Inject

data class ResultadoAuditoria(
    val variante: VarianteNomenclador,
    val totalEnBase: Int,
    val totalEsperado: Int,
    val faltantes: List<String>,
    val creadasPorTi: List<String>,
    val deOtraVariante: List<String>,
    val nombresDistintos: List<String>,
    val naturalezaDistinta: List<String>,
    val madreDistinta: List<String>,
    val huerfanas: List<String>,
    val duplicadas: List<String>
) {
    /** True si no hay ningún problema (las cuentas creadas por el usuario no cuentan como problema). */
    val sinProblemas: Boolean
        get() = faltantes.isEmpty() && nombresDistintos.isEmpty() && naturalezaDistinta.isEmpty() &&
            madreDistinta.isEmpty() && huerfanas.isEmpty() && duplicadas.isEmpty() && deOtraVariante.isEmpty()
}

/**
 * Auditoría de solo lectura: compara las cuentas guardadas en el teléfono con el nomenclador
 * (variante elegida). No modifica nada.
 */
class AuditoriaRepository @Inject constructor(
    private val cuentas: CuentaDao
) {
    suspend fun ejecutar(variante: VarianteNomenclador): ResultadoAuditoria {
        val enBase = cuentas.listarTodas()
        val plan = PlanBase.cuentas(variante)
        val esperados = plan.associateBy { it.codigo }
        val otra = if (variante == VarianteNomenclador.ESTATAL) {
            VarianteNomenclador.NO_ESTATAL
        } else {
            VarianteNomenclador.ESTATAL
        }
        val soloOtra = PlanBase.cuentas(otra).map { it.codigo }.toSet() - esperados.keys

        val duplicadas = enBase.groupBy { it.codigo }.filterValues { it.size > 1 }.keys.sorted()
        val base = enBase.associateBy { it.codigo }
        val idACodigo = enBase.associate { it.id to it.codigo }

        val faltantes = esperados.keys.filter { it !in base }.sorted()
        val extra = base.keys.filter { it !in esperados }
        val deOtra = extra.filter { it in soloOtra }.sorted()
        val propias = extra.filter { it !in soloOtra }.sorted()

        val nombres = mutableListOf<String>()
        val naturalezas = mutableListOf<String>()
        val madres = mutableListOf<String>()
        for ((codigo, esperada) in esperados) {
            val b = base[codigo] ?: continue
            if (b.nombre != esperada.nombre) nombres += "$codigo: «${b.nombre}» en lugar de «${esperada.nombre}»"
            if (b.naturaleza != esperada.naturaleza) {
                naturalezas += "$codigo: ${b.naturaleza.name.lowercase()} en lugar de ${esperada.naturaleza.name.lowercase()}"
            }
            val madreBase = b.cuentaPadreId?.let { idACodigo[it] }
            if (madreBase != esperada.padre) {
                madres += "$codigo: madre ${madreBase ?: "ninguna"} en lugar de ${esperada.padre ?: "ninguna"}"
            }
        }
        val huerfanas = enBase
            .filter { it.cuentaPadreId != null && it.cuentaPadreId !in idACodigo }
            .map { it.codigo }.sorted()

        return ResultadoAuditoria(
            variante = variante,
            totalEnBase = enBase.size,
            totalEsperado = esperados.size,
            faltantes = faltantes,
            creadasPorTi = propias,
            deOtraVariante = deOtra,
            nombresDistintos = nombres.sorted(),
            naturalezaDistinta = naturalezas.sorted(),
            madreDistinta = madres.sorted(),
            huerfanas = huerfanas,
            duplicadas = duplicadas
        )
    }
}
