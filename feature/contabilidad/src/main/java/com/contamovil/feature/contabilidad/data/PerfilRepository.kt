package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.PerfilDao
import com.contamovil.core.database.entity.PerfilEntidadEntity
import com.contamovil.core.database.entity.TipoEntidad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PerfilRepository @Inject constructor(
    private val dao: PerfilDao
) {
    /** Tipo de entidad elegido, o null si aún no se ha definido. */
    val tipo: Flow<TipoEntidad?> = dao.observar().map { it?.tipoEntidad }

    suspend fun guardar(tipo: TipoEntidad) {
        dao.guardar(PerfilEntidadEntity(tipoEntidad = tipo, actualizadoEn = System.currentTimeMillis()))
    }
}
