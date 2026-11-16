package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NormativaDao {

    /** Las normas ya existentes (mismo código) no se tocan. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarNormas(normas: List<NormaEntity>)

    @Insert
    suspend fun insertarVersiones(versiones: List<CuentaVersionEntity>)

    @Insert
    suspend fun insertarCambios(cambios: List<CambioNormativoEntity>)

    @Query("SELECT COUNT(*) FROM cuenta_versiones WHERE normaOrigenCodigo = :norma")
    suspend fun contarVersiones(norma: String): Int

    @Query("SELECT COUNT(*) FROM cambios_normativos WHERE normaCodigo = :norma")
    suspend fun contarCambios(norma: String): Int

    @Query("SELECT * FROM normas ORDER BY anio, numero")
    fun observarNormas(): Flow<List<NormaEntity>>

    @Query("SELECT * FROM cambios_normativos ORDER BY normaCodigo, cuentaCodigo, id")
    fun observarCambios(): Flow<List<CambioNormativoEntity>>

    /** Historial completo de una cuenta, de la versión más antigua a la más reciente. */
    @Query(
        """
        SELECT * FROM cuenta_versiones
        WHERE cuentaCodigo = :codigo
        ORDER BY (vigenteDesde IS NULL) DESC, vigenteDesde, id
        """
    )
    suspend fun versionesDe(codigo: String): List<CuentaVersionEntity>

    /** Versiones de una cuenta aplicables en [fecha] (aaaa-mm-dd). */
    @Query(
        """
        SELECT * FROM cuenta_versiones
        WHERE cuentaCodigo = :codigo
          AND (vigenteDesde IS NULL OR vigenteDesde <= :fecha)
          AND (vigenteHasta IS NULL OR vigenteHasta >= :fecha)
        ORDER BY id
        """
    )
    suspend fun versionesVigentes(codigo: String, fecha: String): List<CuentaVersionEntity>
}
