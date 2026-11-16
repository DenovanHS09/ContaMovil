package com.contamovil.feature.contabilidad.data

import android.database.sqlite.SQLiteConstraintException
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.NormativaDao
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NormativaRepository @Inject constructor(
    private val dao: NormativaDao,
    private val cuentas: CuentaDao
) {
    val normas: Flow<List<NormaEntity>> = dao.observarNormas()
    val cambios: Flow<List<CambioNormativoEntity>> = dao.observarCambios()

    suspend fun versionesDe(codigo: String): List<CuentaVersionEntity> = dao.versionesDe(codigo)

    suspend fun versionesVigentes(codigo: String, fecha: String): List<CuentaVersionEntity> =
        dao.versionesVigentes(codigo, fecha)

    /**
     * Carga la normativa y completa las cuentas de la Res. 407/2019 en una base ya creada.
     * Es idempotente y solo añade: no modifica, renombra ni borra datos existentes.
     */
    suspend fun inicializar() {
        dao.insertarNormas(NormativaBase.normas)

        val versiones = NormativaBase.versiones()
        for (norma in listOf(NormativaBase.R494, NormativaBase.R407)) {
            if (dao.contarVersiones(norma) == 0) {
                dao.insertarVersiones(versiones.filter { it.normaOrigenCodigo == norma })
            }
        }
        for (norma in listOf(NormativaBase.R407, NormativaBase.R166)) {
            if (dao.contarCambios(norma) == 0) {
                dao.insertarCambios(NormativaBase.cambios.filter { it.normaCodigo == norma })
            }
        }

        aplicarAltas407()
    }

    /**
     * Añade las cuentas y subcuentas de la Res. 407/2019 si el nomenclador ya está cargado
     * (si no lo está, se incluirán al cargarlo). Las cuentas que ya existen no se tocan.
     */
    private suspend fun aplicarAltas407() {
        val plan = PlanBase.cuentas(VarianteNomenclador.ESTATAL)
            .filter { NormativaBase.normaDe(it.codigo) == NormativaBase.R407 }
        val conHijas = plan.mapNotNull { it.padre }.toSet()

        for (c in plan) {
            if (cuentas.idPorCodigo(c.codigo) != null) continue
            val padreId = c.padre?.let { cuentas.idPorCodigo(it) } ?: continue
            val padre = cuentas.obtener(padreId) ?: continue
            try {
                cuentas.insertar(
                    CuentaEntity(
                        codigo = c.codigo,
                        nombre = c.nombre,
                        tipo = c.tipo,
                        naturaleza = c.naturaleza,
                        nivel = padre.nivel + 1,
                        cuentaPadreId = padre.id,
                        aceptaMovimientos = c.codigo !in conHijas,
                        codigoHasta = c.hasta
                    )
                )
            } catch (e: SQLiteConstraintException) {
                // Ya existía: se deja como está
            }
        }

        // La 914 ya existía; con subcuentas obligatorias pasa a no admitir movimientos directos,
        // salvo que ya tenga movimientos registrados (entonces se respeta su uso actual).
        val id914 = cuentas.idPorCodigo("914") ?: return
        val cuenta914 = cuentas.obtener(id914) ?: return
        if (cuenta914.aceptaMovimientos &&
            cuentas.contarHijas(id914) > 0 &&
            cuentas.contarMovimientos(id914) == 0
        ) {
            cuentas.actualizar(cuenta914.copy(aceptaMovimientos = false))
        }
    }
}
