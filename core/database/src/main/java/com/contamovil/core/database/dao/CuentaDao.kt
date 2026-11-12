package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contamovil.core.database.entity.CuentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas ORDER BY codigo")
    fun observarTodas(): Flow<List<CuentaEntity>>

    @Query("SELECT * FROM cuentas WHERE id = :id")
    suspend fun obtener(id: Long): CuentaEntity?

    /** Lanza SQLiteConstraintException si el código ya existe. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(cuenta: CuentaEntity): Long

    @Update
    suspend fun actualizar(cuenta: CuentaEntity)

    @Delete
    suspend fun eliminar(cuenta: CuentaEntity)

    @Query("SELECT COUNT(*) FROM cuentas WHERE cuentaPadreId = :id")
    suspend fun contarHijas(id: Long): Int
}
