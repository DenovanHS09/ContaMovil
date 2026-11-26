package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.ComprobanteResumen
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.LineaDetalle
import com.contamovil.core.database.dao.NuevoAsiento
import com.contamovil.core.database.entity.CuentaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Una línea de un comprobante lista para editarse. */
data class LineaEditable(
    val cuenta: CuentaEntity,
    val debito: Long,
    val credito: Long,
    val descripcion: String
)

data class ComprobanteEditable(
    val numero: Int,
    val fecha: Long,
    val concepto: String,
    val lineas: List<LineaEditable>
)

class ComprobanteRepository @Inject constructor(
    private val dao: ComprobanteDao,
    private val cuentas: CuentaDao
) {
    val resumenes: Flow<List<ComprobanteResumen>> = dao.observarResumenes()

    suspend fun crear(fechaEpochDia: Long, concepto: String, lineas: List<NuevoAsiento>): Long =
        dao.crear(fechaEpochDia, concepto, lineas)

    suspend fun actualizar(id: Long, fechaEpochDia: Long, concepto: String, lineas: List<NuevoAsiento>) =
        dao.actualizar(id, fechaEpochDia, concepto, lineas)

    /** Carga un comprobante con sus cuentas completas para rellenar el formulario de edición. */
    suspend fun cargarParaEditar(id: Long): ComprobanteEditable? {
        val cabecera = dao.obtener(id) ?: return null
        val lineas = dao.asientosDe(id).map { a ->
            val cuenta = cuentas.obtener(a.cuentaId) ?: return null
            LineaEditable(cuenta, a.debito, a.credito, a.descripcion)
        }
        return ComprobanteEditable(cabecera.numero, cabecera.fecha, cabecera.concepto, lineas)
    }

    suspend fun detalle(id: Long): List<LineaDetalle> = dao.detalle(id)

    suspend fun eliminar(id: Long) = dao.eliminar(id)
}
