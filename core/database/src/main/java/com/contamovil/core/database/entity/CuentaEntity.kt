package com.contamovil.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Naturaleza { DEUDORA, ACREEDORA, MIXTA }

enum class TipoCuenta(val naturaleza: Naturaleza) {
    ACTIVO(Naturaleza.DEUDORA),
    PASIVO(Naturaleza.ACREEDORA),
    PATRIMONIO(Naturaleza.ACREEDORA),
    INGRESO(Naturaleza.ACREEDORA),
    GASTO(Naturaleza.DEUDORA),
    PRODUCCION(Naturaleza.DEUDORA),
    CIERRE(Naturaleza.MIXTA)
}

/** Una cuenta del plan de cuentas contable. */
@Entity(
    tableName = "cuentas",
    indices = [Index(value = ["codigo"], unique = true), Index("cuentaPadreId")]
)
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val tipo: TipoCuenta,
    val naturaleza: Naturaleza = tipo.naturaleza,
    /** 1 = grupo, 2 = subgrupo, 3 = cuenta, 4 = subcuenta... */
    val nivel: Int = 1,
    val cuentaPadreId: Long? = null,
    /** Solo las cuentas "hoja" admiten movimientos en comprobantes. */
    @ColumnInfo(defaultValue = "1") val aceptaMovimientos: Boolean = true,
    @ColumnInfo(defaultValue = "1") val activa: Boolean = true
)
