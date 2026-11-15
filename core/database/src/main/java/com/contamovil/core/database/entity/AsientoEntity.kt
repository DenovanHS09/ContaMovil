package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Una línea (apunte) de un comprobante: una cuenta con su importe en el débito o en el crédito.
 * Los importes se guardan en centavos para evitar errores de redondeo.
 */
@Entity(
    tableName = "asientos",
    foreignKeys = [
        ForeignKey(
            entity = ComprobanteEntity::class,
            parentColumns = ["id"],
            childColumns = ["comprobanteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("comprobanteId"), Index("cuentaId")]
)
data class AsientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comprobanteId: Long,
    val cuentaId: Long,
    val debito: Long,
    val credito: Long,
    val descripcion: String
)
