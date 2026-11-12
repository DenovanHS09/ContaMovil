package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.core.database.entity.TipoCuenta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCuentasScreen(vm: PlanCuentasViewModel = hiltViewModel()) {
    val cuentas by vm.cuentas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Plan de cuentas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.limpiarError(); mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva cuenta")
            }
        }
    ) { padding ->
        if (cuentas.isEmpty()) {
            Text(
                "Aún no hay cuentas. Toca + para crear la primera.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(cuentas, key = { it.id }) { cuenta ->
                    ListItem(
                        modifier = Modifier.padding(start = ((cuenta.nivel - 1) * 16).dp),
                        overlineContent = { Text(cuenta.codigo) },
                        headlineContent = { Text(cuenta.nombre) },
                        supportingContent = {
                            Text("${cuenta.tipo.name.lowercase()} · ${cuenta.naturaleza.name.lowercase()}")
                        },
                        trailingContent = {
                            TextButton(onClick = { vm.eliminar(cuenta) }) { Text("Borrar") }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        NuevaCuentaDialog(
            error = error,
            onCancelar = { mostrarDialogo = false },
            onGuardar = { codigo, nombre, tipo ->
                vm.crear(codigo, nombre, tipo) { mostrarDialogo = false }
            }
        )
    }
}

@Composable
private fun NuevaCuentaDialog(
    error: String?,
    onCancelar: () -> Unit,
    onGuardar: (String, String, TipoCuenta) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TipoCuenta.ACTIVO) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = codigo, onValueChange = { codigo = it },
                    label = { Text("Código (ej. 101 o 101.01)") }, singleLine = true
                )
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, singleLine = true
                )
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TipoCuenta.entries.forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipo = t },
                            label = { Text(t.name.lowercase()) }
                        )
                    }
                }
                if (error != null) Text(error)
            }
        },
        confirmButton = {
            TextButton(onClick = { onGuardar(codigo, nombre, tipo) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
