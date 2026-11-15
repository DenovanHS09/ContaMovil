package com.contamovil.core.database.dao

import androidx.room.Dao
import androidx.room.Query

data class LineaDiario(
    val comprobanteId: Long,
    val numero: Int,
    val fecha: Long,
    val concepto: String,
    val codigo: String,
    val cuenta: String,
    val debito: Long,
    val credito: Long
)

data class LineaMayor(
    val numero: Int,
    val fecha: Long,
    val concepto: String,
    val descripcion: String,
    val debito: Long,
    val credito: Long
)

data class FilaBalance(
    val codigo: String,
    val cuenta: String,
    val saldoInicial: Long,
    val debitos: Long,
    val creditos: Long
)

/** Consultas de solo lectura para el libro diario, el mayor y el balance de comprobación. */
@Dao
interface InformeDao {

    @Query(
        """
        SELECT c.id AS comprobanteId, c.numero AS numero, c.fecha AS fecha, c.concepto AS concepto,
               cu.codigo AS codigo, cu.nombre AS cuenta, a.debito AS debito, a.credito AS credito
        FROM asientos a
        JOIN comprobantes c ON c.id = a.comprobanteId
        JOIN cuentas cu ON cu.id = a.cuentaId
        WHERE c.fecha BETWEEN :desde AND :hasta
        ORDER BY c.fecha, c.numero, a.id
        """
    )
    suspend fun diario(desde: Long, hasta: Long): List<LineaDiario>

    /** Saldo (débitos - créditos) de la cuenta antes de la fecha [desde]. */
    @Query(
        """
        SELECT COALESCE(SUM(a.debito - a.credito), 0)
        FROM asientos a
        JOIN comprobantes c ON c.id = a.comprobanteId
        WHERE a.cuentaId = :cuentaId AND c.fecha < :desde
        """
    )
    suspend fun saldoAnterior(cuentaId: Long, desde: Long): Long

    @Query(
        """
        SELECT c.numero AS numero, c.fecha AS fecha, c.concepto AS concepto,
               a.descripcion AS descripcion, a.debito AS debito, a.credito AS credito
        FROM asientos a
        JOIN comprobantes c ON c.id = a.comprobanteId
        WHERE a.cuentaId = :cuentaId AND c.fecha BETWEEN :desde AND :hasta
        ORDER BY c.fecha, c.numero, a.id
        """
    )
    suspend fun mayor(cuentaId: Long, desde: Long, hasta: Long): List<LineaMayor>

    @Query(
        """
        SELECT cu.codigo AS codigo, cu.nombre AS cuenta,
               COALESCE(SUM(CASE WHEN c.fecha < :desde THEN a.debito - a.credito ELSE 0 END), 0) AS saldoInicial,
               COALESCE(SUM(CASE WHEN c.fecha >= :desde THEN a.debito ELSE 0 END), 0) AS debitos,
               COALESCE(SUM(CASE WHEN c.fecha >= :desde THEN a.credito ELSE 0 END), 0) AS creditos
        FROM asientos a
        JOIN comprobantes c ON c.id = a.comprobanteId
        JOIN cuentas cu ON cu.id = a.cuentaId
        WHERE c.fecha <= :hasta
        GROUP BY cu.id
        ORDER BY cu.codigo
        """
    )
    suspend fun balance(desde: Long, hasta: Long): List<FilaBalance>
}
