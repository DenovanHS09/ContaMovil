package com.contamovil.feature.contabilidad.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import com.contamovil.core.database.entity.TipoEntidad
import com.contamovil.feature.contabilidad.data.CuentaRepository
import com.contamovil.feature.contabilidad.data.PerfilRepository
import com.contamovil.feature.contabilidad.data.varianteNomenclador
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Una fila visible del árbol de cuentas. */
data class FilaCuenta(
    val cuenta: CuentaEntity,
    val tieneHijas: Boolean,
    val expandida: Boolean
)

@HiltViewModel
class PlanCuentasViewModel @Inject constructor(
    private val repo: CuentaRepository,
    private val perfil: PerfilRepository
) : ViewModel() {

    private val _expandidas = MutableStateFlow<Set<Long>>(emptySet())
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    /** Tipo de entidad elegido (null si aún no se ha definido). */
    val tipoEntidad: StateFlow<TipoEntidad?> = perfil.tipo
        .stateIn<TipoEntidad?>(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Filas que se muestran: solo las ramas desplegadas, o los resultados planos si hay
     * búsqueda. Se calcula fuera del hilo principal. Es null mientras cargan las cuentas.
     */
    val filas: StateFlow<List<FilaCuenta>?> =
        combine(repo.cuentas, _expandidas, _busqueda) { cuentas, expandidas, q ->
            construirFilas(cuentas, expandidas, q)
        }
            .flowOn(Dispatchers.Default)
            .stateIn<List<FilaCuenta>?>(viewModelScope, SharingStarted.Lazily, null)

    /** Todas las cuentas (en orden de árbol): sirven para proponer la cuenta madre. */
    val cuentas: StateFlow<List<CuentaEntity>> = repo.cuentas
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Cuentas cargadas antes de guardar rangos: copia el rango de su nombre a su columna
        viewModelScope.launch { repo.completarRangos() }
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun limpiarError() { _error.value = null }

    fun setBusqueda(texto: String) { _busqueda.value = texto }

    fun alternar(id: Long) {
        _expandidas.update { if (id in it) it - id else it + id }
    }

    /** [onExito] se invoca solo si la cuenta se guardó. */
    fun crear(
        codigo: String,
        nombre: String,
        tipo: TipoCuenta,
        madre: CuentaEntity?,
        referencia: CuentaEntity?,
        copiarSubcuentas: Boolean,
        onExito: () -> Unit
    ) {
        if (codigo.isBlank() || nombre.isBlank()) {
            _error.value = "Código y nombre son obligatorios"
            return
        }
        viewModelScope.launch {
            val ancestros = repo.crear(codigo, nombre, tipo, madre, referencia, copiarSubcuentas)
            if (ancestros != null) {
                // Despliega las ramas madre para que la cuenta nueva quede visible
                _expandidas.update { it + ancestros }
                _error.value = null
                onExito()
            } else {
                _error.value = "Ya existe una cuenta con el código $codigo"
            }
        }
    }

    fun eliminar(cuenta: CuentaEntity) {
        viewModelScope.launch {
            repo.eliminar(cuenta)?.let { _error.value = it }
        }
    }

    /** Guarda el tipo de entidad y carga el nomenclador con la variante de patrimonio que le corresponde. */
    fun cargarNomenclador(tipo: TipoEntidad) {
        viewModelScope.launch {
            perfil.guardar(tipo)
            repo.cargarNomenclador(tipo.varianteNomenclador())
        }
    }

    private fun construirFilas(
        cuentas: List<CuentaEntity>,
        expandidas: Set<Long>,
        busqueda: String
    ): List<FilaCuenta> {
        val q = busqueda.trim()
        if (q.isNotEmpty()) {
            return cuentas
                .filter { it.codigo.contains(q, ignoreCase = true) || it.nombre.contains(q, ignoreCase = true) }
                .map { FilaCuenta(it, tieneHijas = false, expandida = false) }
        }

        val padres = HashSet<Long>()
        for (c in cuentas) c.cuentaPadreId?.let { padres.add(it) }

        // La lista llega en orden de árbol: la madre siempre va antes que sus hijas.
        val visibles = HashSet<Long>()
        val resultado = ArrayList<FilaCuenta>()
        for (c in cuentas) {
            val padre = c.cuentaPadreId
            val visible = padre == null || (padre in visibles && padre in expandidas)
            if (visible) {
                visibles.add(c.id)
                resultado.add(FilaCuenta(c, c.id in padres, c.id in expandidas))
            }
        }
        return resultado
    }
}
