package com.contamovil.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contamovil.core.database.dao.ComprobanteDao
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.entity.AsientoEntity
import com.contamovil.core.database.entity.ComprobanteEntity
import com.contamovil.core.database.entity.CuentaEntity

@Database(
    entities = [CuentaEntity::class, ComprobanteEntity::class, AsientoEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun comprobanteDao(): ComprobanteDao

    companion object {
        const val NOMBRE = "contamovil.db"
    }
}
