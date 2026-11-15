package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.ComprobanteResumen
import com.contamovil.core.database.dao.LineaDetalle
import com.contamovil.core.database.dao.NuevoAsiento
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ComprobanteRepository @Inject constructor(
    private val dao: ComprobanteDao
) {
    val resumenes: Flow<List<ComprobanteResumen>> = dao.observarResumenes()

    suspend fun crear(fechaEpochDia: Long, concepto: String, lineas: List<NuevoAsiento>): Long =
        dao.crear(fechaEpochDia, concepto, lineas)

    suspend fun detalle(id: Long): List<LineaDetalle> = dao.detalle(id)

    suspend fun eliminar(id: Long) = dao.eliminar(id)
}
