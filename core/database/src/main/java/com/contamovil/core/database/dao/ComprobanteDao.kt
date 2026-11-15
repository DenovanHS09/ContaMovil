package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.contamovil.core.database.entity.AsientoEntity
import com.contamovil.core.database.entity.ComprobanteEntity
import kotlinx.coroutines.flow.Flow

data class NuevoAsiento(
    val cuentaId: Long,
    val debito: Long,
    val credito: Long,
    val descripcion: String
)

data class ComprobanteResumen(
    val id: Long,
    val numero: Int,
    val fecha: Long,
    val concepto: String,
    val total: Long
)

data class LineaDetalle(
    val codigo: String,
    val cuenta: String,
    val debito: Long,
    val credito: Long,
    val descripcion: String
)

@Dao
abstract class ComprobanteDao {

    @Insert
    protected abstract suspend fun insertarComprobante(comprobante: ComprobanteEntity): Long

    @Insert
    protected abstract suspend fun insertarAsientos(asientos: List<AsientoEntity>)

    @Query("SELECT COALESCE(MAX(numero), 0) + 1 FROM comprobantes")
    protected abstract suspend fun siguienteNumero(): Int

    /** Guarda el comprobante y todas sus líneas en una sola transacción. */
    @Transaction
    open suspend fun crear(fecha: Long, concepto: String, lineas: List<NuevoAsiento>): Long {
        require(lineas.sumOf { it.debito } == lineas.sumOf { it.credito }) {
            "El comprobante no cuadra: débitos distintos de créditos"
        }
        val id = insertarComprobante(
            ComprobanteEntity(
                numero = siguienteNumero(),
                fecha = fecha,
                concepto = concepto,
                creadoEn = System.currentTimeMillis()
            )
        )
        insertarAsientos(
            lineas.map {
                AsientoEntity(
                    comprobanteId = id,
                    cuentaId = it.cuentaId,
                    debito = it.debito,
                    credito = it.credito,
                    descripcion = it.descripcion
                )
            }
        )
        return id
    }

    /** Borra el comprobante; sus líneas se borran en cascada. */
    @Query("DELETE FROM comprobantes WHERE id = :id")
    abstract suspend fun eliminar(id: Long)

    @Query(
        """
        SELECT c.id AS id, c.numero AS numero, c.fecha AS fecha, c.concepto AS concepto,
               COALESCE(SUM(a.debito), 0) AS total
        FROM comprobantes c
        LEFT JOIN asientos a ON a.comprobanteId = c.id
        GROUP BY c.id
        ORDER BY c.fecha DESC, c.numero DESC
        """
    )
    abstract fun observarResumenes(): Flow<List<ComprobanteResumen>>

    @Query(
        """
        SELECT cu.codigo AS codigo, cu.nombre AS cuenta, a.debito AS debito,
               a.credito AS credito, a.descripcion AS descripcion
        FROM asientos a
        JOIN cuentas cu ON cu.id = a.cuentaId
        WHERE a.comprobanteId = :id
        ORDER BY a.id
        """
    )
    abstract suspend fun detalle(id: Long): List<LineaDetalle>
}
