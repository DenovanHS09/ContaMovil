package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contamovil.core.database.entity.PerfilEntidadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {

    @Query("SELECT * FROM perfil_entidad WHERE id = 1")
    fun observar(): Flow<PerfilEntidadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(perfil: PerfilEntidadEntity)
}
