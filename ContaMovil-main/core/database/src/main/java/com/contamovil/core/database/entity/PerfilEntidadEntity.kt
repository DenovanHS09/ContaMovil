package com.contamovil.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoEntidad {
    MIPYME_PRIVADA,
    MIPYME_ESTATAL,
    TRABAJADOR_CUENTA_PROPIA,
    OTRA_ESTATAL,
    OTRA_NO_ESTATAL
}

/** Perfil de la entidad que lleva la contabilidad. Hay una sola fila (id = 1). */
@Entity(tableName = "perfil_entidad")
data class PerfilEntidadEntity(
    @PrimaryKey val id: Long = 1,
    val tipoEntidad: TipoEntidad,
    val actualizadoEn: Long
)
