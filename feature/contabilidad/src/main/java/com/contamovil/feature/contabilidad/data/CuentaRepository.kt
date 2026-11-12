package com.contamovil.feature.contabilidad.data

import android.database.sqlite.SQLiteConstraintException
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CuentaRepository @Inject constructor(
    private val dao: CuentaDao
) {
    /** Cuentas ordenadas como árbol: cada cuenta justo debajo de su madre. */
    val cuentas: Flow<List<CuentaEntity>> = dao.observarTodas().map { ordenarArbol(it) }

    /**
     * Crea una cuenta. Si el código tiene punto (ej. "135.0010"), la cuenta madre es la
     * que tiene el código sin el último tramo ("135"), si existe.
     * Devuelve false si el código ya existe.
     */
    suspend fun crear(codigo: String, nombre: String, tipo: TipoCuenta): Boolean {
        val cod = codigo.trim()
        val codigoPadre = cod.substringBeforeLast('.', "")
        val padre = if (codigoPadre.isNotEmpty()) {
            dao.idPorCodigo(codigoPadre)?.let { dao.obtener(it) }
        } else null
        return try {
            dao.insertar(
                CuentaEntity(
                    codigo = cod,
                    nombre = nombre.trim(),
                    tipo = tipo,
                    nivel = (padre?.nivel ?: 0) + 1,
                    cuentaPadreId = padre?.id
                )
            )
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun eliminar(cuenta: CuentaEntity): Boolean {
        if (dao.contarHijas(cuenta.id) > 0) return false
        dao.eliminar(cuenta)
        return true
    }

    /**
     * Carga el nomenclador de cuentas cubano. Salta las cuentas cuyo código ya existe,
     * así que se puede repetir sin duplicar. Devuelve cuántas cuentas nuevas creó.
     */
    suspend fun cargarNomenclador(variante: VarianteNomenclador): Int {
        val plan = PlanBase.cuentas(variante)
        val conHijas = plan.mapNotNull { it.padre }.toSet()
        val ids = mutableMapOf<String, Long>()
        val niveles = mutableMapOf<String, Int>()
        var nuevas = 0
        for (c in plan) {
            niveles[c.codigo] = (c.padre?.let { niveles[it] } ?: 0) + 1
            val existente = dao.idPorCodigo(c.codigo)
            if (existente != null) {
                ids[c.codigo] = existente
                continue
            }
            val id = dao.insertar(
                CuentaEntity(
                    codigo = c.codigo,
                    nombre = c.nombre,
                    tipo = c.tipo,
                    naturaleza = c.naturaleza,
                    nivel = niveles.getValue(c.codigo),
                    cuentaPadreId = c.padre?.let { ids[it] },
                    aceptaMovimientos = c.codigo !in conHijas
                )
            )
            ids[c.codigo] = id
            nuevas++
        }
        return nuevas
    }

    private fun ordenarArbol(todas: List<CuentaEntity>): List<CuentaEntity> {
        val hijos = todas.groupBy { it.cuentaPadreId }
        val resultado = ArrayList<CuentaEntity>(todas.size)
        val vistos = HashSet<Long>()
        fun visitar(padreId: Long?) {
            for (c in hijos[padreId].orEmpty().sortedBy { it.codigo }) {
                if (vistos.add(c.id)) {
                    resultado.add(c)
                    visitar(c.id)
                }
            }
        }
        visitar(null)
        todas.filter { it.id !in vistos }.forEach { resultado.add(it) }
        return resultado
    }
}
