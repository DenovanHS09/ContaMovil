package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoCambio {
    ALTA_CUENTA,
    ALTA_SUBCUENTAS,
    MODIFICA_USO_CONTENIDO,
    MODIFICA_DENOMINACION,
    MODIFICA_NATURALEZA
}

/** Qué cambió una norma en una cuenta, con su fuente jurídica. */
@Entity(
    tableName = "cambios_normativos",
    indices = [Index("cuentaCodigo"), Index("normaCodigo")]
)
data class CambioNormativoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cuentaCodigo: String,
    val normaCodigo: String,
    val tipoCambio: TipoCambio,
    val valorAnterior: String? = null,
    val valorNuevo: String? = null,
    val estado: EstadoNormativo,
    val observaciones: String? = null
)
