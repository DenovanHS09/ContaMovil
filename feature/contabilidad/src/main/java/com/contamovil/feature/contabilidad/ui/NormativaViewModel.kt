package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.NormativaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class NormativaViewModel @Inject constructor(
    private val repo: NormativaRepository
) : ViewModel() {

    val normas: StateFlow<List<NormaEntity>> = repo.normas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cambios: StateFlow<List<CambioNormativoEntity>> = repo.cambios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _versiones = MutableStateFlow<List<CuentaVersionEntity>?>(null)
    val versiones: StateFlow<List<CuentaVersionEntity>?> = _versiones

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Historial de una cuenta; con [fecha] (dd/mm/aaaa) solo las versiones aplicables ese día. */
    fun consultar(codigo: String, fecha: String) {
        val cod = codigo.trim()
        if (cod.isEmpty()) {
            _error.value = "Escribe el código de la cuenta"
            return
        }
        val iso: String? = if (fecha.isBlank()) {
            null
        } else {
            try {
                LocalDate.parse(fecha.trim(), FORMATO_FECHA).toString()
            } catch (e: DateTimeParseException) {
                _error.value = "Fecha inválida. Usa dd/mm/aaaa o déjala vacía"
                return
            }
        }
        _error.value = null
        viewModelScope.launch {
            _versiones.value =
                if (iso == null) repo.versionesDe(cod) else repo.versionesVigentes(cod, iso)
        }
    }
}
