package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import com.contamovil.feature.contabilidad.data.sugerirMadre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCuentasScreen(vm: PlanCuentasViewModel = hiltViewModel()) {
    val filas by vm.filas.collectAsStateWithLifecycle()
    val todas by vm.cuentas.collectAsStateWithLifecycle()
    val tipoEntidad by vm.tipoEntidad.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarNomenclador by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuentas") },
                actions = {
                    TextButton(onClick = { mostrarNomenclador = true }) {
                        Text("Nomenclador")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.limpiarError(); mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva cuenta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "Estructura contable",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        "Consulta, organiza y administra las cuentas de tu entidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.86f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                Text(
                                    todas.size.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    "Cuentas",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                Text(
                                    todas.count { it.cuentaPadreId == null }.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    "Principales",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::setBusqueda,
                label = { Text("Buscar por código o nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            val lista = filas
            when {
                lista == null -> Unit
                lista.isEmpty() && busqueda.isBlank() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Todavía no hay cuentas", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Carga el nomenclador cubano desde el botón superior o crea una cuenta con el botón +.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                lista.isEmpty() -> Text(
                    "No encontramos cuentas que coincidan con la búsqueda.",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 12.dp, end = 12.dp, top = 2.dp, bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(lista, key = { it.cuenta.id }) { fila ->
                        FilaCuentaItem(
                            fila = fila,
                            enBusqueda = busqueda.isNotBlank(),
                            onAlternar = { vm.alternar(fila.cuenta.id) },
                            onEliminar = { vm.eliminar(fila.cuenta) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        NuevaCuentaDialog(
            error = error,
            cuentas = todas,
            onCancelar = { mostrarDialogo = false },
            onGuardar = { codigo, nombre, tipo, madre, referencia, copiar ->
                vm.crear(codigo, nombre, tipo, madre, referencia, copiar) {
                    mostrarDialogo = false
                }
            }
        )
    }

    val mensaje = error
    if (mensaje != null && !mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { vm.limpiarError() },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = { vm.limpiarError() }) { Text("Entendido") } }
        )
    }

    if (mostrarNomenclador) {
        SelectorTipoEntidadDialog(
            titulo = "Nomenclador de cuentas (Cuba)",
            explicacion = "Carga el nomenclador de la Resolución 494/2016 y sus modificaciones. " +
                "Elige tu tipo de entidad: define la variante del patrimonio (grupo 30). " +
                "Si ya existen cuentas con el mismo código, no se tocan.",
            actual = tipoEntidad,
            textoConfirmar = "Cargar",
            onElegir = { vm.cargarNomenclador(it); mostrarNomenclador = false },
            onCerrar = { mostrarNomenclador = false }
        )
    }
}

/** Tarjeta de cuenta dentro del árbol jerárquico. */
@Composable
private fun FilaCuentaItem(
    fila: FilaCuenta,
    enBusqueda: Boolean,
    onAlternar: () -> Unit,
    onEliminar: () -> Unit
) {
    val cuenta = fila.cuenta
    val sangria = if (enBusqueda) 0 else ((cuenta.nivel - 1).coerceAtLeast(0) * 12)

    Card(
        modifier = Modifier.fillMaxWidth().padding(start = sangria.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (cuenta.nivel <= 1) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.padding(start = 10.dp).clickable(
                    enabled = fila.tieneHijas,
                    onClick = onAlternar
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    when {
                        !fila.tieneHijas -> "•"
                        fila.expandida -> "−"
                        else -> "+"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier.weight(1f).clickable(
                    enabled = fila.tieneHijas,
                    onClick = onAlternar
                ).padding(horizontal = 10.dp, vertical = 11.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    cuenta.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        cuenta.codigo,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "  ·  ${cuenta.tipo.name.lowercase()}  ·  ${cuenta.naturaleza.name.lowercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Borrar cuenta",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NuevaCuentaDialog(
    error: String?,
    cuentas: List<CuentaEntity>,
    onCancelar: () -> Unit,
    onGuardar: (String, String, TipoCuenta, CuentaEntity?, CuentaEntity?, Boolean) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var tipoElegido by remember { mutableStateOf<TipoCuenta?>(null) }
    var madreManual by remember { mutableStateOf<CuentaEntity?>(null) }
    var manual by remember { mutableStateOf(false) }
    var copiarSub by remember { mutableStateOf(true) }
    var eligiendoMadre by remember { mutableStateOf(false) }

    val sug = remember(codigo, cuentas) { sugerirMadre(codigo, cuentas) }
    val madre = if (manual) madreManual else sug.madre
    val referencia = if (manual) null else sug.referencia
    val tipo = tipoElegido ?: referencia?.tipo ?: madre?.tipo ?: TipoCuenta.ACTIVO
    val nSub = remember(referencia, cuentas) {
        if (referencia == null) 0 else cuentas.count { it.cuentaPadreId == referencia.id }
    }

    val aviso: String? = when {
        manual -> madre?.let { "Se creará dentro de: ${it.codigo} ${it.nombre}" }
            ?: "Se creará como cuenta principal (sin madre)"
        referencia != null ->
            "Está en el rango ${referencia.codigo}–${referencia.codigoHasta ?: referencia.codigo}. " +
                (madre?.let { "Se creará dentro de: ${it.codigo} ${it.nombre}" } ?: "")
        madre != null -> "Se creará dentro de: ${madre.codigo} ${madre.nombre}"
        sug.madreFaltante != null ->
            "No existe la cuenta madre ${sug.madreFaltante}. Elige otra con \"Cambiar madre\"."
        sug.fueraDeRango ->
            "Aviso: este código no está en ningún rango del nomenclador. Puedes guardarla igual."
        else -> null
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva cuenta") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = codigo, onValueChange = { codigo = it },
                    label = { Text("Código (ej. 102 o 135.0070)") }, singleLine = true
                )
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, singleLine = true
                )
                if (aviso != null) {
                    Text(aviso, style = MaterialTheme.typography.bodySmall)
                }
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(onClick = { eligiendoMadre = true }) { Text("Cambiar madre") }
                    TextButton(onClick = { manual = true; madreManual = null }) { Text("Sin madre") }
                    if (manual) {
                        TextButton(onClick = { manual = false; madreManual = null }) { Text("Automática") }
                    }
                }
                if (!manual && nSub > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = copiarSub, onCheckedChange = { copiarSub = it })
                        Text("Crear también sus $nSub subcuentas obligatorias")
                    }
                }
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoCuenta.entries.forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipoElegido = t },
                            label = { Text(t.name.lowercase()) }
                        )
                    }
                }
                if (error != null) Text(error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(codigo, nombre, tipo, madre, referencia, copiarSub && nSub > 0)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )

    if (eligiendoMadre) {
        SelectorCuentaDialog(
            cuentas = cuentas,
            titulo = "Elegir cuenta madre",
            onElegir = { madreManual = it; manual = true; eligiendoMadre = false },
            onCerrar = { eligiendoMadre = false }
        )
    }
}
