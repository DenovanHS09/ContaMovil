package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import com.contamovil.feature.contabilidad.data.CuentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanCuentasViewModel @Inject constructor(
    private val repo: CuentaRepository
) : ViewModel() {

    val cuentas: StateFlow<List<CuentaEntity>> = repo.cuentas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun limpiarError() { _error.value = null }

    /** [onExito] se invoca solo si la cuenta se guardó. */
    fun crear(codigo: String, nombre: String, tipo: TipoCuenta, onExito: () -> Unit) {
        if (codigo.isBlank() || nombre.isBlank()) {
            _error.value = "Código y nombre son obligatorios"
            return
        }
        viewModelScope.launch {
            if (repo.crear(codigo, nombre, tipo)) {
                _error.value = null
                onExito()
            } else {
                _error.value = "Ya existe una cuenta con el código $codigo"
            }
        }
    }

    fun eliminar(cuenta: CuentaEntity) {
        viewModelScope.launch {
            if (!repo.eliminar(cuenta)) {
                _error.value = "No se puede eliminar: tiene subcuentas"
            }
        }
    }
}
