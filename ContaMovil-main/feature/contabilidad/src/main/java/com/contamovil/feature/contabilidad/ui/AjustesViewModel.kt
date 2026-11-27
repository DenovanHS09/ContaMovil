package com.contamovil.feature.contabilidad.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.RespaldoManager
import com.contamovil.core.database.entity.TipoEntidad
import com.contamovil.feature.contabilidad.data.PerfilRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AjustesUi(
    val ultimoRespaldo: Long = 0L,
    val trabajando: Boolean = false,
    val mensaje: String? = null,
    /** Archivo elegido que espera la confirmación del usuario para restaurar. */
    val pendiente: Uri? = null,
    /** Si no es null, la app debe cerrarse (la base de datos quedó cerrada). */
    val reinicio: String? = null
)

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val manager: RespaldoManager,
    private val perfil: PerfilRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AjustesUi(ultimoRespaldo = manager.ultimoRespaldo()))
    val ui: StateFlow<AjustesUi> = _ui

    val tipoEntidad: StateFlow<TipoEntidad?> = perfil.tipo
        .stateIn<TipoEntidad?>(viewModelScope, SharingStarted.Lazily, null)

    fun guardarTipoEntidad(tipo: TipoEntidad) {
        viewModelScope.launch { perfil.guardar(tipo) }
    }

    fun exportar(destino: Uri) {
        _ui.update { it.copy(trabajando = true, mensaje = null) }
        viewModelScope.launch {
            val resultado = manager.exportar(destino)
            _ui.update {
                it.copy(
                    trabajando = false,
                    ultimoRespaldo = manager.ultimoRespaldo(),
                    mensaje = resultado.fold(
                        onSuccess = { "Respaldo guardado correctamente." },
                        onFailure = { e -> "No se pudo crear el respaldo: ${e.message}" }
                    )
                )
            }
        }
    }

    fun pedirRestaurar(origen: Uri) = _ui.update { it.copy(pendiente = origen, mensaje = null) }

    fun cancelarRestaurar() = _ui.update { it.copy(pendiente = null) }

    fun confirmarRestaurar() {
        val origen = _ui.value.pendiente ?: return
        _ui.update { it.copy(pendiente = null, trabajando = true, mensaje = null) }
        viewModelScope.launch {
            val resultado = manager.restaurar(origen)
            _ui.update {
                when {
                    resultado.isSuccess -> it.copy(
                        trabajando = false,
                        reinicio = "Datos restaurados correctamente. La app se reiniciará para cargarlos."
                    )
                    !manager.baseAbierta() -> it.copy(
                        trabajando = false,
                        reinicio = "La restauración no se completó (${resultado.exceptionOrNull()?.message}). " +
                            "La app debe reiniciarse."
                    )
                    else -> it.copy(
                        trabajando = false,
                        mensaje = "No se pudo restaurar: ${resultado.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun reiniciar() = manager.reiniciarApp()
}
