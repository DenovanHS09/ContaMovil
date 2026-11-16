package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class InstrumentoNorma {
    NOMENCLADOR_EMPRESARIAL,
    NOMENCLADOR_PRESUPUESTADO,
    CLASIFICADOR_OBJETO_GASTO,
    PROFORMAS_ESTADOS_FINANCIEROS,
    NORMA_CONTABLE,
    OTRA_RELACIONADA
}

enum class EstadoNorma { VIGENTE, MODIFICADA, DEROGADA, SIN_VERIFICAR }

/** Cuánto de la norma se ha podido comprobar contra una fuente oficial. */
enum class VerificacionNorma {
    TEXTO_OFICIAL_LEIDO,
    TEXTO_PARCIAL,
    SOLO_FICHA_GACETA,
    SOLO_REFERENCIA_EN_OTRA_NORMA,
    SOLO_DOCUMENTO_INFORMATIVO,
    SIN_VERIFICAR
}

/** Una disposición jurídica relacionada con el nomenclador. Las fechas van en formato ISO (aaaa-mm-dd). */
@Entity(tableName = "normas", indices = [Index(value = ["codigo"], unique = true)])
data class NormaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Identificador interno estable, p. ej. "RES-494-2016". */
    val codigo: String,
    val numero: Int,
    val anio: Int,
    val organismo: String,
    val titulo: String,
    val gaceta: String,
    /** Identificador de la Gaceta Oficial, p. ej. "GOC-2016-977-EX39". */
    val identificador: String? = null,
    val fechaFirma: String? = null,
    val fechaPublicacion: String? = null,
    val fechaVigencia: String? = null,
    val instrumento: InstrumentoNorma,
    val estado: EstadoNorma,
    val derogadaPorCodigo: String? = null,
    val verificacion: VerificacionNorma,
    val observaciones: String? = null
)
