package com.contamovil.feature.contabilidad.data

import android.database.sqlite.SQLiteConstraintException
import com.contamovil.core.database.dao.CuentaDao
import com.contamovil.core.database.dao.NormativaDao
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.RevisionNormativaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NormativaRepository @Inject constructor(
    private val dao: NormativaDao,
    private val cuentas: CuentaDao
) {
    val normas: Flow<List<NormaEntity>> = dao.observarNormas()
    val cambios: Flow<List<CambioNormativoEntity>> = dao.observarCambios()
    val revisiones: Flow<List<RevisionNormativaEntity>> = dao.observarRevisiones()

    suspend fun versionesDe(codigo: String): List<CuentaVersionEntity> = dao.versionesDe(codigo)

    suspend fun versionesVigentes(codigo: String, fecha: String): List<CuentaVersionEntity> =
        dao.versionesVigentes(codigo, fecha)

    /**
     * Carga y revisa la normativa. Es idempotente y solo añade o corrige con registro de lo anterior
     * en `normas_revisiones`; nunca borra datos. Cada paso es independiente: si uno falla, los demás
     * se ejecutan igualmente.
     */
    suspend fun inicializar() {
        paso { dao.insertarNormas(NormativaBase.normas + NormativaRevision.normasNuevas) }
        paso {
            val versiones = NormativaBase.versiones()
            for (norma in listOf(NormativaBase.R494, NormativaBase.R407)) {
                if (dao.contarVersiones(norma) == 0) {
                    dao.insertarVersiones(versiones.filter { it.normaOrigenCodigo == norma })
                }
            }
        }
        paso {
            for (norma in listOf(NormativaBase.R407, NormativaBase.R166)) {
                if (dao.contarCambios(norma) == 0) {
                    dao.insertarCambios(NormativaBase.cambios.filter { it.normaCodigo == norma })
                }
            }
        }
        paso { aplicarAltas407() }
        paso { revisarNormas() }
        paso { fijarVigencia494() }
        paso { if (dao.contarSeguros() == 0) dao.insertarVersiones(NormativaRevision.versionesSeguros()) }
        paso { corregirDenominaciones() }
        paso { aplicarResumenesFase1() }
        paso { completar865() }
    }

    private suspend fun paso(bloque: suspend () -> Unit) {
        try {
            bloque()
        } catch (e: Exception) {
            // Un paso fallido no impide los siguientes; se reintentará en el próximo arranque.
        }
    }

    private suspend fun registrar(
        clave: String, tabla: String, registro: String, campo: String,
        anterior: String?, nuevo: String?, motivo: String, fuente: String
    ) {
        dao.insertarRevision(
            RevisionNormativaEntity(
                clave = clave, tabla = tabla, registro = registro, campo = campo,
                valorAnterior = anterior, valorNuevo = nuevo, motivo = motivo, fuente = fuente,
                aplicadoEn = System.currentTimeMillis()
            )
        )
    }

    private val camposNorma: List<Pair<String, (NormaEntity) -> String?>> = listOf(
        "gaceta" to { n -> n.gaceta },
        "identificador" to { n -> n.identificador },
        "fechaFirma" to { n -> n.fechaFirma },
        "fechaPublicacion" to { n -> n.fechaPublicacion },
        "fechaVigencia" to { n -> n.fechaVigencia },
        "estado" to { n -> n.estado.name },
        "derogadaPorCodigo" to { n -> n.derogadaPorCodigo },
        "verificacion" to { n -> n.verificacion.name },
        "observaciones" to { n -> n.observaciones }
    )

    /** Completa los metadatos de las normas con lo comprobado en los PDF oficiales. */
    private suspend fun revisarNormas() {
        for (codigo in NormativaRevision.codigosAjustados) {
            val actual = dao.norma(codigo) ?: continue
            val nuevo = NormativaRevision.ajustarNorma(actual)
            if (nuevo == actual) continue
            for ((campo, leer) in camposNorma) {
                val antes = leer(actual)
                val despues = leer(nuevo)
                if (antes != despues) {
                    registrar(
                        "NORMA:$codigo:$campo:v6", "normas", codigo, campo, antes, despues,
                        "Verificado contra el texto oficial de la Gaceta Oficial", "Gaceta Oficial (PDF)"
                    )
                }
            }
            dao.actualizarNorma(nuevo)
        }
    }

    private suspend fun fijarVigencia494() {
        val n = dao.fijarVigenciaDesde(NormativaBase.R494, NormativaRevision.DESDE_494)
        if (n > 0) {
            registrar(
                "VERSIONES:${NormativaBase.R494}:vigenteDesde:v6", "cuenta_versiones",
                "${NormativaBase.R494} ($n versiones)", "vigenteDesde", null, NormativaRevision.DESDE_494,
                "Res. 494/2016, disposición séptima: se aplica a las operaciones desde el inicio del ejercicio contable 2017",
                "Gaceta Oficial No. 39 Extraordinaria de 2016"
            )
        }
    }

    private suspend fun corregirDenominaciones() {
        for (c in NormativaRevision.correcciones) {
            val n = dao.corregirDenominacion(c.codigo, NormativaBase.R494, c.variante, c.anterior, c.oficial)
            if (n > 0) {
                val sufijo = c.variante?.let { ":$it" } ?: ""
                registrar(
                    "VERSION:${NormativaBase.R494}:${c.codigo}$sufijo:denominacion:v6", "cuenta_versiones",
                    c.codigo, "denominacion", c.anterior, c.oficial,
                    "Tipografía oficial del Anexo 1 (guion); solo afecta al historial", "Res. 494/2016, Anexo 1"
                )
            }
        }
    }

    private suspend fun aplicarResumenesFase1() {
        for ((codigo, r) in NormativaRevision.resumenesFase1) {
            val n = dao.fijarResumen(codigo, NormativaBase.R494, r.uso, r.debitos, r.creditos)
            if (n > 0) {
                registrar(
                    "VERSION:${NormativaBase.R494}:$codigo:usoContenido:v6", "cuenta_versiones", codigo,
                    "usoContenido", null, r.uso,
                    "Resumen redactado del Anexo 2 (fase 1: cuentas nominales y de cierre)", "Res. 494/2016, Anexo 2"
                )
            }
        }
    }

    /** Añade la redacción de la Res. 166/2021 para las cuentas 865-866 sin retirar la original. */
    private suspend fun completar865() {
        if (dao.contarVersiones(NormativaBase.R166) == 0) {
            dao.insertarVersiones(listOf(NormativaRevision.version865Rev166()))
        }
        val n = dao.completarCambio(
            "865", NormativaBase.R166, NormativaRevision.USO_865_166, NormativaRevision.OBS_CAMBIO_865
        )
        if (n > 0) {
            registrar(
                "CAMBIO:${NormativaBase.R166}:865:valorNuevo:v6", "cambios_normativos", "865",
                "valorNuevo", null, NormativaRevision.USO_865_166,
                "Texto oficial de la Res. 166/2021 leído; se completa el cambio antes registrado sin valor",
                "Gaceta Oficial No. 75 Ordinaria de 2021"
            )
        }
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
