package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.entity.CambioNormativoEntity
import com.contamovil.core.database.entity.CuentaVersionEntity
import com.contamovil.core.database.entity.NormaEntity
import com.contamovil.core.database.entity.RevisionNormativaEntity
import com.contamovil.core.database.entity.TipoEntidad
import com.contamovil.feature.contabilidad.data.AuditoriaRepository
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.NormativaRepository
import com.contamovil.feature.contabilidad.data.PerfilRepository
import com.contamovil.feature.contabilidad.data.ResultadoAuditoria
import com.contamovil.feature.contabilidad.data.VarianteNomenclador
import com.contamovil.feature.contabilidad.data.varianteNomenclador
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
    private val repo: NormativaRepository,
    private val auditoriaRepo: AuditoriaRepository,
    perfil: PerfilRepository
) : ViewModel() {

    val normas: StateFlow<List<NormaEntity>> = repo.normas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cambios: StateFlow<List<CambioNormativoEntity>> = repo.cambios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val revisiones: StateFlow<List<RevisionNormativaEntity>> = repo.revisiones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tipoEntidad: StateFlow<TipoEntidad?> = perfil.tipo
        .stateIn<TipoEntidad?>(viewModelScope, SharingStarted.Lazily, null)

    private val _versiones = MutableStateFlow<List<CuentaVersionEntity>?>(null)
    val versiones: StateFlow<List<CuentaVersionEntity>?> = _versiones

    private val _auditoria = MutableStateFlow<ResultadoAuditoria?>(null)
    val auditoria: StateFlow<ResultadoAuditoria?> = _auditoria

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

    /** Auditoría de solo lectura de las cuentas guardadas frente al nomenclador. */
    fun auditar() {
        val variante = tipoEntidad.value?.varianteNomenclador() ?: VarianteNomenclador.ESTATAL
        viewModelScope.launch { _auditoria.value = auditoriaRepo.ejecutar(variante) }
    }
}
