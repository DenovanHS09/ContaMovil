package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Cabecera de un comprobante de operaciones. Moneda única: CUP. */
@Entity(
    tableName = "comprobantes",
    indices = [Index(value = ["numero"], unique = true)]
)
data class ComprobanteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numero: Int,
    /** Fecha como día de época (LocalDate.toEpochDay). */
    val fecha: Long,
    val concepto: String,
    val creadoEn: Long
)
