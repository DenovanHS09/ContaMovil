package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.FilaBalance
import com.contamovil.core.database.dao.InformeDao
import com.contamovil.core.database.dao.LineaDiario
import com.contamovil.core.database.dao.LineaMayor
import javax.inject.Inject

class InformeRepository @Inject constructor(
    private val dao: InformeDao
) {
    suspend fun diario(desde: Long, hasta: Long): List<LineaDiario> = dao.diario(desde, hasta)

    suspend fun saldoAnterior(cuentaId: Long, desde: Long): Long = dao.saldoAnterior(cuentaId, desde)

    suspend fun mayor(cuentaId: Long, desde: Long, hasta: Long): List<LineaMayor> =
        dao.mayor(cuentaId, desde, hasta)

    suspend fun balance(desde: Long, hasta: Long): List<FilaBalance> = dao.balance(desde, hasta)
}
