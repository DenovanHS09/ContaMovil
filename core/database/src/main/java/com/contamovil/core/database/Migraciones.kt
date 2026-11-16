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

/** v2 -> v3: guarda el rango de cada cuenta del nomenclador (columna opcional). */
val MIGRACION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `cuentas` ADD COLUMN `codigoHasta` TEXT")
    }
}

/**
 * v3 -> v4: normativa del nomenclador (normas, versiones de cuenta y cambios normativos).
 * Solo crea tablas nuevas: no modifica ni borra ningún dato existente.
 */
val MIGRACION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `normas` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`codigo` TEXT NOT NULL, " +
                "`numero` INTEGER NOT NULL, " +
                "`anio` INTEGER NOT NULL, " +
                "`organismo` TEXT NOT NULL, " +
                "`titulo` TEXT NOT NULL, " +
                "`gaceta` TEXT NOT NULL, " +
                "`identificador` TEXT, " +
                "`fechaFirma` TEXT, " +
                "`fechaPublicacion` TEXT, " +
                "`fechaVigencia` TEXT, " +
                "`instrumento` TEXT NOT NULL, " +
                "`estado` TEXT NOT NULL, " +
                "`derogadaPorCodigo` TEXT, " +
                "`verificacion` TEXT NOT NULL, " +
                "`observaciones` TEXT)"
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_normas_codigo` ON `normas` (`codigo`)")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `cuenta_versiones` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`cuentaCodigo` TEXT NOT NULL, " +
                "`variante` TEXT, " +
                "`denominacion` TEXT NOT NULL, " +
                "`naturaleza` TEXT NOT NULL, " +
                "`grupo` TEXT NOT NULL, " +
                "`subgrupo` TEXT NOT NULL, " +
                "`usoContenido` TEXT, " +
                "`funcionamientoDebitos` TEXT, " +
                "`funcionamientoCreditos` TEXT, " +
                "`normaOrigenCodigo` TEXT NOT NULL, " +
                "`vigenteDesde` TEXT, " +
                "`vigenteHasta` TEXT, " +
                "`estadoNormativo` TEXT NOT NULL, " +
                "`observaciones` TEXT)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_cuenta_versiones_cuentaCodigo` " +
                "ON `cuenta_versiones` (`cuentaCodigo`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_cuenta_versiones_normaOrigenCodigo` " +
                "ON `cuenta_versiones` (`normaOrigenCodigo`)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `cambios_normativos` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`cuentaCodigo` TEXT NOT NULL, " +
                "`normaCodigo` TEXT NOT NULL, " +
                "`tipoCambio` TEXT NOT NULL, " +
                "`valorAnterior` TEXT, " +
                "`valorNuevo` TEXT, " +
                "`estado` TEXT NOT NULL, " +
                "`observaciones` TEXT)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_cambios_normativos_cuentaCodigo` " +
                "ON `cambios_normativos` (`cuentaCodigo`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_cambios_normativos_normaCodigo` " +
                "ON `cambios_normativos` (`normaCodigo`)"
        )
    }
}
