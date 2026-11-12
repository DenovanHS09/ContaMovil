package com.contamovil.feature.contabilidad.data

import android.database.sqlite.SQLiteConstraintException
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CuentaRepository @Inject constructor(
    private val dao: CuentaDao
) {
    val cuentas: Flow<List<CuentaEntity>> = dao.observarTodas()

    /**
     * Crea una cuenta. El nivel se deduce de los puntos del código
     * (ej. "101" = nivel 1, "101.01" = nivel 2).
     * Devuelve false si el código ya existe.
     */
    suspend fun crear(codigo: String, nombre: String, tipo: TipoCuenta): Boolean =
        try {
            dao.insertar(
                CuentaEntity(
                    codigo = codigo.trim(),
                    nombre = nombre.trim(),
                    tipo = tipo,
                    nivel = codigo.count { it == '.' } + 1
                )
            )
            true
        } catch (e: SQLiteConstraintException) {
            false
        }

    suspend fun eliminar(cuenta: CuentaEntity): Boolean {
        if (dao.contarHijas(cuenta.id) > 0) return false
        dao.eliminar(cuenta)
        return true
    }
}
