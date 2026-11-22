package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registro de cada corrección aplicada a datos normativos: qué valía antes, qué vale ahora,
 * por qué y con qué fuente. Permite auditar y revertir a mano. La [clave] es única, de modo que
 * una corrección nunca se registra dos veces.
 */
@Entity(tableName = "normas_revisiones", indices = [Index(value = ["clave"], unique = true)])
data class RevisionNormativaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clave: String,
    val tabla: String,
    val registro: String,
    val campo: String,
    val valorAnterior: String? = null,
    val valorNuevo: String? = null,
    val motivo: String,
    val fuente: String,
    val aplicadoEn: Long
)
