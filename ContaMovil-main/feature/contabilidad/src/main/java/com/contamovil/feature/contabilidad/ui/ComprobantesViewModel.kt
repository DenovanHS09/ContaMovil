package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.dao.ComprobanteResumen
import com.contamovil.core.database.dao.LineaDetalle
import com.contamovil.core.database.dao.NuevoAsiento
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.feature.contabilidad.data.ComprobanteRepository
import com.contamovil.feature.contabilidad.data.CuentaRepository
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.centavosATexto
import com.contamovil.feature.contabilidad.data.formatCentavos
import com.contamovil.feature.contabilidad.data.parseCentavos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class LineaForm(
    val id: Int,
    val cuenta: CuentaEntity? = null,
    val debito: String = "",
    val credito: String = "",
    val descripcion: String = ""
) {
    val vacia: Boolean
        get() = cuenta == null && debito.isBlank() && credito.isBlank() && descripcion.isBlank()
}

data class FormState(
    val fecha: String,
    val concepto: String,
    val lineas: List<LineaForm>,
    val error: String? = null,
    /** Id del comprobante que se está editando; null si es uno nuevo. */
    val editandoId: Long? = null,
    val numero: Int? = null
) {
    val totalDebito: Long get() = lineas.sumOf { parseCentavos(it.debito) ?: 0L }
    val totalCredito: Long get() = lineas.sumOf { parseCentavos(it.credito) ?: 0L }
    val cuadra: Boolean get() = totalDebito > 0 && totalDebito == totalCredito

    companion object {
        fun nuevo() = FormState(
            fecha = LocalDate.now().format(FORMATO_FECHA),
            concepto = "",
            lineas = listOf(LineaForm(1), LineaForm(2))
        )
    }
}

@HiltViewModel
class ComprobantesViewModel @Inject constructor(
    private val repo: ComprobanteRepository,
    cuentasRepo: CuentaRepository
) : ViewModel() {

    val comprobantes: StateFlow<List<ComprobanteResumen>> = repo.resumenes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Solo las cuentas que admiten movimientos (hojas del árbol). */
    val cuentasMovimiento: StateFlow<List<CuentaEntity>> = cuentasRepo.cuentas
        .map { lista -> lista.filter { it.aceptaMovimientos && it.activa } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(FormState.nuevo())
    val form: StateFlow<FormState> = _form

    private var siguienteId = 3

    fun nuevo() {
        siguienteId = 3
        _form.value = FormState.nuevo()
    }

    /** Carga un comprobante existente en el formulario. [onListo] se llama cuando ya se puede mostrar. */
    fun editar(comprobante: ComprobanteResumen, onListo: () -> Unit) {
        viewModelScope.launch {
            val e = repo.cargarParaEditar(comprobante.id) ?: return@launch
            val lineas = e.lineas.mapIndexed { i, l ->
                LineaForm(
                    id = i + 1,
                    cuenta = l.cuenta,
                    debito = if (l.debito > 0) centavosATexto(l.debito) else "",
                    credito = if (l.credito > 0) centavosATexto(l.credito) else "",
                    descripcion = l.descripcion
                )
            }.toMutableList()
            while (lineas.size < 2) lineas.add(LineaForm(lineas.size + 1))
            siguienteId = lineas.size + 1
            _form.value = FormState(
                fecha = LocalDate.ofEpochDay(e.fecha).format(FORMATO_FECHA),
                concepto = e.concepto,
                lineas = lineas,
                editandoId = comprobante.id,
                numero = e.numero
            )
            onListo()
        }
    }

    fun setFecha(v: String) = _form.update { it.copy(fecha = v, error = null) }
    fun setConcepto(v: String) = _form.update { it.copy(concepto = v, error = null) }

    fun agregarLinea() = _form.update {
        it.copy(lineas = it.lineas + LineaForm(siguienteId++), error = null)
    }

    fun quitarLinea(id: Int) = _form.update {
        if (it.lineas.size <= 2) it
        else it.copy(lineas = it.lineas.filterNot { l -> l.id == id }, error = null)
    }

    fun cambiarLinea(nueva: LineaForm) = _form.update {
        it.copy(lineas = it.lineas.map { l -> if (l.id == nueva.id) nueva else l }, error = null)
    }

    fun elegirCuenta(lineaId: Int, cuenta: CuentaEntity) = _form.update {
        it.copy(
            lineas = it.lineas.map { l -> if (l.id == lineaId) l.copy(cuenta = cuenta) else l },
            error = null
        )
    }

    private fun setError(mensaje: String) = _form.update { it.copy(error = mensaje) }

    /** Valida el formulario y, si todo cuadra, guarda el comprobante. */
    fun guardar(onExito: () -> Unit) {
        val f = _form.value

        val fecha = try {
            LocalDate.parse(f.fecha.trim(), FORMATO_FECHA)
        } catch (e: DateTimeParseException) {
            setError("Fecha inválida. Usa el formato dd/mm/aaaa")
            return
        }
        if (f.concepto.isBlank()) {
            setError("Escribe el concepto del comprobante")
            return
        }

        val usadas = f.lineas.filterNot { it.vacia }
        if (usadas.size < 2) {
            setError("Un comprobante necesita al menos dos líneas")
            return
        }

        val asientos = mutableListOf<NuevoAsiento>()
        for ((i, l) in usadas.withIndex()) {
            val n = i + 1
            val cuenta = l.cuenta
            if (cuenta == null) {
                setError("Línea $n: elige una cuenta")
                return
            }
            val d = parseCentavos(l.debito)
            val c = parseCentavos(l.credito)
            if ((l.debito.isNotBlank() && d == null) || (l.credito.isNotBlank() && c == null)) {
                setError("Línea $n: importe inválido (máximo 2 decimales)")
                return
            }
            if ((d == null) == (c == null)) {
                setError("Línea $n: pon el importe en débito o en crédito, pero no en ambos")
                return
            }
            asientos += NuevoAsiento(cuenta.id, d ?: 0L, c ?: 0L, l.descripcion.trim())
        }

        val td = asientos.sumOf { it.debito }
        val tc = asientos.sumOf { it.credito }
        if (td != tc) {
            setError("No cuadra: débitos ${formatCentavos(td)} y créditos ${formatCentavos(tc)}")
            return
        }

        viewModelScope.launch {
            val id = f.editandoId
            if (id == null) {
                repo.crear(fecha.toEpochDay(), f.concepto.trim(), asientos)
            } else {
                repo.actualizar(id, fecha.toEpochDay(), f.concepto.trim(), asientos)
            }
            onExito()
        }
    }

    suspend fun detalle(id: Long): List<LineaDetalle> = repo.detalle(id)

    fun eliminar(id: Long) {
        viewModelScope.launch { repo.eliminar(id) }
    }
}
