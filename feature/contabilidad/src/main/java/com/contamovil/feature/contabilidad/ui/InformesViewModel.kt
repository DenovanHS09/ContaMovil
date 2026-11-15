package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.dao.FilaBalance
import com.contamovil.core.database.dao.LineaDiario
import com.contamovil.core.database.dao.LineaMayor
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.feature.contabilidad.data.CuentaRepository
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.InformeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class MayorFila(val linea: LineaMayor, val saldo: Long)

data class MayorUi(
    val saldoAnterior: Long,
    val filas: List<MayorFila>,
    val totalDebitos: Long,
    val totalCreditos: Long,
    val saldoFinal: Long
)

@HiltViewModel
class InformesViewModel @Inject constructor(
    private val repo: InformeRepository,
    cuentasRepo: CuentaRepository
) : ViewModel() {

    private val hoy = LocalDate.now()

    private val _desde = MutableStateFlow(LocalDate.of(hoy.year, 1, 1).format(FORMATO_FECHA))
    val desde: StateFlow<String> = _desde

    private val _hasta = MutableStateFlow(hoy.format(FORMATO_FECHA))
    val hasta: StateFlow<String> = _hasta

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _diario = MutableStateFlow<List<LineaDiario>>(emptyList())
    val diario: StateFlow<List<LineaDiario>> = _diario

    private val _mayor = MutableStateFlow<MayorUi?>(null)
    val mayor: StateFlow<MayorUi?> = _mayor

    private val _balance = MutableStateFlow<List<FilaBalance>>(emptyList())
    val balance: StateFlow<List<FilaBalance>> = _balance

    private val _cuentaMayor = MutableStateFlow<CuentaEntity?>(null)
    val cuentaMayor: StateFlow<CuentaEntity?> = _cuentaMayor

    val cuentas: StateFlow<List<CuentaEntity>> = cuentasRepo.cuentas
        .map { lista -> lista.filter { it.aceptaMovimientos && it.activa } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setDesde(v: String) { _desde.value = v }
    fun setHasta(v: String) { _hasta.value = v }
    fun elegirCuentaMayor(c: CuentaEntity) { _cuentaMayor.value = c }

    private fun parseFecha(texto: String): Long? = try {
        LocalDate.parse(texto.trim(), FORMATO_FECHA).toEpochDay()
    } catch (e: DateTimeParseException) {
        null
    }

    /** Recalcula el informe de la vista indicada: 0 = diario, 1 = mayor, 2 = balance. */
    fun actualizar(vista: Int) {
        val d = parseFecha(_desde.value)
        val h = parseFecha(_hasta.value)
        if (d == null || h == null) {
            _error.value = "Fecha inválida. Usa el formato dd/mm/aaaa"
            return
        }
        if (h < d) {
            _error.value = "La fecha final no puede ser anterior a la inicial"
            return
        }
        _error.value = null

        viewModelScope.launch {
            when (vista) {
                0 -> _diario.value = repo.diario(d, h)
                1 -> {
                    val cuenta = _cuentaMayor.value
                    _mayor.value = if (cuenta == null) null else construirMayor(cuenta.id, d, h)
                }
                else -> _balance.value = repo.balance(d, h)
            }
        }
    }

    private suspend fun construirMayor(cuentaId: Long, desde: Long, hasta: Long): MayorUi {
        val anterior = repo.saldoAnterior(cuentaId, desde)
        var saldo = anterior
        val filas = repo.mayor(cuentaId, desde, hasta).map { l ->
            saldo += l.debito - l.credito
            MayorFila(l, saldo)
        }
        return MayorUi(
            saldoAnterior = anterior,
            filas = filas,
            totalDebitos = filas.sumOf { it.linea.debito },
            totalCreditos = filas.sumOf { it.linea.credito },
            saldoFinal = saldo
        )
    }
}
