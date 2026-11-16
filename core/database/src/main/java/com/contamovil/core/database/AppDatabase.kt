package com.contamovil.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.InformeDao
import com.contamovil.core.database.dao.NormativaDao
import com.contamovil.core.database.entity.AsientoEntity
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.ComprobanteEntity
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity

@Database(
    entities = [
        CuentaEntity::class,
        ComprobanteEntity::class,
        AsientoEntity::class,
        NormaEntity::class,
        CuentaVersionEntity::class,
        CambioNormativoEntity::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun comprobanteDao(): ComprobanteDao
    abstract fun informeDao(): InformeDao
    abstract fun normativaDao(): NormativaDao

    companion object {
        const val NOMBRE = "contamovil.db"
        /** Versión actual del esquema. Cambiarla aquí al añadir una migración. */
        const val VERSION = 4
    }
}
