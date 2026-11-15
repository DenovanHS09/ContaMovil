package com.contamovil.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** v1 -> v2: añade comprobantes y asientos. No toca la tabla de cuentas. */
val MIGRACION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `comprobantes` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`numero` INTEGER NOT NULL, " +
                "`fecha` INTEGER NOT NULL, " +
                "`concepto` TEXT NOT NULL, " +
                "`creadoEn` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_comprobantes_numero` " +
                "ON `comprobantes` (`numero`)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `asientos` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`comprobanteId` INTEGER NOT NULL, " +
                "`cuentaId` INTEGER NOT NULL, " +
                "`debito` INTEGER NOT NULL, " +
                "`credito` INTEGER NOT NULL, " +
                "`descripcion` TEXT NOT NULL, " +
                "FOREIGN KEY(`comprobanteId`) REFERENCES `comprobantes`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`cuentaId`) REFERENCES `cuentas`(`id`) " +
                "ON UPDATE NO ACTION ON DELETE RESTRICT)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_asientos_comprobanteId` " +
                "ON `asientos` (`comprobanteId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_asientos_cuentaId` " +
                "ON `asientos` (`cuentaId`)"
        )
    }
}
