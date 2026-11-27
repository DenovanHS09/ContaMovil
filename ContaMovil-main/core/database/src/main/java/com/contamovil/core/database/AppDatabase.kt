package com.contamovil.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.InformeDao
import com.contamovil.core.database.dao.NormativaDao
import com.contamovil.core.database.dao.PerfilDao
import com.contamovil.core.database.entity.AsientoEntity
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.ComprobanteEntity
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.PerfilEntidadEntity
import com.contamovil.core.database.entity.RevisionNormativaEntity

@Database(
    entities = [
        CuentaEntity::class,
        ComprobanteEntity::class,
        AsientoEntity::class,
        NormaEntity::class,
        CuentaVersionEntity::class,
        CambioNormativoEntity::class,
        PerfilEntidadEntity::class,
        RevisionNormativaEntity::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun comprobanteDao(): ComprobanteDao
    abstract fun informeDao(): InformeDao
    abstract fun normativaDao(): NormativaDao
    abstract fun perfilDao(): PerfilDao

    companion object {
        const val NOMBRE = "contamovil.db"
        /** Versión actual del esquema. Cambiarla aquí al añadir una migración. */
        const val VERSION = 7
    }
}
