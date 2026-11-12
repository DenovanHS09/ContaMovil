package com.contamovil.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.entity.CuentaEntity

@Database(
    entities = [CuentaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao

    companion object {
        const val NOMBRE = "contamovil.db"
    }
}
