package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoNormativo { CONFIRMADO, PENDIENTE_INTERPRETACION }

/**
 * Una versión normativa de una cuenta: qué decía el nomenclador de ella y desde cuándo.
 * Se vincula por código (no por id) para que el historial sobreviva aunque la cuenta cambie.
 */
@Entity(
    tableName = "cuenta_versiones",
    indices = [Index("cuentaCodigo"), Index("normaOrigenCodigo")]
)
data class CuentaVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cuentaCodigo: String,
    /** "ESTATAL" o "NO_ESTATAL" cuando la redacción difiere según el tipo de entidad; null si aplica a ambos. */
    val variante: String? = null,
    val denominacion: String,
    val naturaleza: Naturaleza,
    val grupo: String,
    val subgrupo: String,
    /** Resumen del uso y contenido, redactado a partir del anexo (no es transcripción literal). */
    val usoContenido: String? = null,
    val funcionamientoDebitos: String? = null,
    val funcionamientoCreditos: String? = null,
    val normaOrigenCodigo: String,
    val vigenteDesde: String? = null,
    val vigenteHasta: String? = null,
    val estadoNormativo: EstadoNormativo,
    val observaciones: String? = null
)
