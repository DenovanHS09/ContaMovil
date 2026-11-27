package com.contamovil.feature.contabilidad.data

import android.database.sqlite.SQLiteConstraintException
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CuentaRepository @Inject constructor(
    private val dao: CuentaDao
) {
    /** Cuentas ordenadas como árbol: cada cuenta justo debajo de su madre. */
    val cuentas: Flow<List<CuentaEntity>> = dao.observarTodas()
        .map { ordenarArbol(it) }
        .flowOn(Dispatchers.Default)

    /**
     * Crea una cuenta dentro de [madre] (null = cuenta principal).
     * Si hay [referencia] (la cuenta del nomenclador cuyo rango contiene el código), hereda su
     * naturaleza y, con [copiarSubcuentas], también sus subcuentas obligatorias.
     *
     * Devuelve null si el código ya existe; si se creó, devuelve los ids de las cuentas madre
     * (de la más cercana a la más lejana) para desplegarlas en pantalla.
     */
    suspend fun crear(
        codigo: String,
        nombre: String,
        tipo: TipoCuenta,
        madre: CuentaEntity?,
        referencia: CuentaEntity?,
        copiarSubcuentas: Boolean
    ): List<Long>? {
        val cod = codigo.trim()
        val nivel = (madre?.nivel ?: 0) + 1
        val plantillas = if (copiarSubcuentas && referencia != null) dao.hijas(referencia.id) else emptyList()

        val id = try {
            dao.insertar(
                CuentaEntity(
                    codigo = cod,
                    nombre = nombre.trim(),
                    tipo = tipo,
                    naturaleza = referencia?.naturaleza ?: tipo.naturaleza,
                    nivel = nivel,
                    cuentaPadreId = madre?.id,
                    // Con subcuentas obligatorias, los movimientos van en ellas
                    aceptaMovimientos = plantillas.isEmpty()
                )
            )
        } catch (e: SQLiteConstraintException) {
            return null
        }

        for (h in plantillas) {
            val sufijo = h.codigo.substringAfter('.', "")
            if (sufijo.isEmpty()) continue
            try {
                dao.insertar(
                    CuentaEntity(
                        codigo = "$cod.$sufijo",
                        nombre = h.nombre,
                        tipo = h.tipo,
                        naturaleza = h.naturaleza,
                        nivel = nivel + 1,
                        cuentaPadreId = id
                    )
                )
            } catch (e: SQLiteConstraintException) {
                // Esa subcuenta ya existía: se deja como está
            }
        }

        val ancestros = mutableListOf<Long>()
        var actual = madre
        while (actual != null) {
            ancestros.add(actual.id)
            actual = actual.cuentaPadreId?.let { dao.obtener(it) }
        }
        return ancestros
    }

    /** Devuelve null si se eliminó, o el motivo por el que no se pudo. */
    suspend fun eliminar(cuenta: CuentaEntity): String? {
        if (dao.contarHijas(cuenta.id) > 0) return "No se puede eliminar: tiene subcuentas"
        if (dao.contarMovimientos(cuenta.id) > 0) {
            return "No se puede eliminar: tiene movimientos en comprobantes"
        }
        dao.eliminar(cuenta)
        return null
    }

    /**
     * Carga el nomenclador de cuentas cubano. Salta las cuentas cuyo código ya existe (solo les
     * completa el rango si no lo tenían), así que se puede repetir sin duplicar.
     * Devuelve cuántas cuentas nuevas creó.
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
                if (c.hasta != null) {
                    dao.obtener(existente)?.let { ex ->
                        if (ex.codigoHasta == null) dao.actualizar(ex.copy(codigoHasta = c.hasta))
                    }
                }
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
                    aceptaMovimientos = c.codigo !in conHijas,
                    codigoHasta = c.hasta
                )
            )
            ids[c.codigo] = id
            nuevas++
        }
        return nuevas
    }

    private val marcaRango = Regex("""\[(\d+)-(\d+)\]\s*$""")

    /**
     * Las cuentas cargadas antes de existir la columna de rango llevan el rango en el nombre
     * ("Efectivo en Caja [101-108]"). Lo copia a su columna una sola vez.
     */
    suspend fun completarRangos() {
        for (c in dao.sinRangoConMarca()) {
            val m = marcaRango.find(c.nombre) ?: continue
            dao.actualizar(c.copy(codigoHasta = m.groupValues[2]))
        }
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
