package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.RevisionNormativaEntity
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

    /** Devuelve -1 si la revisión ya estaba registrada (clave repetida). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarRevision(revision: RevisionNormativaEntity): Long

    @Query("SELECT * FROM normas WHERE codigo = :codigo")
    suspend fun norma(codigo: String): NormaEntity?

    @Update
    suspend fun actualizarNorma(norma: NormaEntity)

    @Query("SELECT COUNT(*) FROM cuenta_versiones WHERE normaOrigenCodigo = :norma")
    suspend fun contarVersiones(norma: String): Int

    @Query("SELECT COUNT(*) FROM cuenta_versiones WHERE variante = 'SEGUROS'")
    suspend fun contarSeguros(): Int

    @Query("SELECT COUNT(*) FROM cambios_normativos WHERE normaCodigo = :norma")
    suspend fun contarCambios(norma: String): Int

    /** Fija la fecha de inicio solo en las versiones de la norma que aún no la tenían. */
    @Query(
        "UPDATE cuenta_versiones SET vigenteDesde = :desde " +
            "WHERE normaOrigenCodigo = :norma AND vigenteDesde IS NULL"
    )
    suspend fun fijarVigenciaDesde(norma: String, desde: String): Int

    /** Completa el resumen de uso y contenido solo donde aún no existe. */
    @Query(
        "UPDATE cuenta_versiones SET usoContenido = :uso, funcionamientoDebitos = :debitos, " +
            "funcionamientoCreditos = :creditos " +
            "WHERE cuentaCodigo = :codigo AND normaOrigenCodigo = :norma " +
            "AND usoContenido IS NULL AND variante IS NULL"
    )
    suspend fun fijarResumen(
        codigo: String, norma: String, uso: String, debitos: String?, creditos: String?
    ): Int

    /** Corrige la denominación solo si todavía vale exactamente [anterior]. */
    @Query(
        "UPDATE cuenta_versiones SET denominacion = :oficial " +
            "WHERE cuentaCodigo = :codigo AND normaOrigenCodigo = :norma " +
            "AND IFNULL(variante, '') = IFNULL(:variante, '') AND denominacion = :anterior"
    )
    suspend fun corregirDenominacion(
        codigo: String, norma: String, variante: String?, anterior: String, oficial: String
    ): Int

    /** Completa un cambio registrado sin valor nuevo. */
    @Query(
        "UPDATE cambios_normativos SET valorNuevo = :nuevo, observaciones = :observaciones " +
            "WHERE cuentaCodigo = :cuenta AND normaCodigo = :norma AND valorNuevo IS NULL"
    )
    suspend fun completarCambio(cuenta: String, norma: String, nuevo: String, observaciones: String): Int

    @Query("SELECT * FROM normas ORDER BY anio, numero")
    fun observarNormas(): Flow<List<NormaEntity>>

    @Query("SELECT * FROM cambios_normativos ORDER BY normaCodigo, cuentaCodigo, id")
    fun observarCambios(): Flow<List<CambioNormativoEntity>>

    @Query("SELECT * FROM normas_revisiones ORDER BY id DESC")
    fun observarRevisiones(): Flow<List<RevisionNormativaEntity>>

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
