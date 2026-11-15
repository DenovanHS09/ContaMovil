package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.core.database.dao.ComprobanteResumen
import com.contamovil.core.database.dao.LineaDetalle
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.formatCentavos
import java.time.LocalDate
import kotlin.math.abs

private fun fechaTexto(epochDia: Long): String =
    LocalDate.ofEpochDay(epochDia).format(FORMATO_FECHA)

/** Lista de comprobantes registrados. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprobantesScreen(vm: ComprobantesViewModel, onNuevo: () -> Unit) {
    val lista by vm.comprobantes.collectAsStateWithLifecycle()
    var seleccionado by remember { mutableStateOf<ComprobanteResumen?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Comprobantes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo comprobante")
            }
        }
    ) { padding ->
        if (lista.isEmpty()) {
            Text(
                "Aún no hay comprobantes. Toca + para registrar el primero.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(lista, key = { it.id }) { c ->
                    ListItem(
                        modifier = Modifier.clickable { seleccionado = c },
                        overlineContent = { Text("N.º ${c.numero} · ${fechaTexto(c.fecha)}") },
                        headlineContent = { Text(c.concepto) },
                        trailingContent = { Text("${formatCentavos(c.total)} CUP") }
                    )
                }
            }
        }
    }

    seleccionado?.let { c ->
        DetalleComprobanteDialog(vm = vm, comprobante = c, onCerrar = { seleccionado = null })
    }
}

@Composable
private fun DetalleComprobanteDialog(
    vm: ComprobantesViewModel,
    comprobante: ComprobanteResumen,
    onCerrar: () -> Unit
) {
    var lineas by remember { mutableStateOf<List<LineaDetalle>?>(null) }
    LaunchedEffect(comprobante.id) { lineas = vm.detalle(comprobante.id) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Comprobante N.º ${comprobante.numero}") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("${fechaTexto(comprobante.fecha)} · ${comprobante.concepto}")
                lineas?.forEach { l ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("${l.codigo} ${l.cuenta}")
                            if (l.descripcion.isNotBlank()) {
                                Text(l.descripcion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text(
                            if (l.debito > 0) "D ${formatCentavos(l.debito)}"
                            else "C ${formatCentavos(l.credito)}"
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
        dismissButton = {
            TextButton(onClick = { vm.eliminar(comprobante.id); onCerrar() }) { Text("Eliminar") }
        }
    )
}

/** Formulario para registrar un comprobante con partida doble. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoComprobanteScreen(vm: ComprobantesViewModel, onTerminar: () -> Unit) {
    val form by vm.form.collectAsStateWithLifecycle()
    val cuentas by vm.cuentasMovimiento.collectAsStateWithLifecycle()
    var lineaEligiendo by remember { mutableStateOf<Int?>(null) }

    val td = form.totalDebito
    val tc = form.totalCredito

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo comprobante") },
                navigationIcon = {
                    IconButton(onClick = onTerminar) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar")
                    }
                },
                actions = {
                    TextButton(onClick = { vm.guardar(onTerminar) }) { Text("Guardar") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = form.fecha, onValueChange = vm::setFecha,
                        label = { Text("Fecha (dd/mm/aaaa)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.concepto, onValueChange = vm::setConcepto,
                        label = { Text("Concepto") }, modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            items(form.lineas, key = { it.id }) { l ->
                LineaEditor(
                    linea = l,
                    puedeQuitar = form.lineas.size > 2,
                    onElegirCuenta = { lineaEligiendo = l.id },
                    onCambio = vm::cambiarLinea,
                    onQuitar = { vm.quitarLinea(l.id) }
                )
            }
            item {
                TextButton(onClick = vm::agregarLinea) { Text("+ Añadir línea") }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Débitos: ${formatCentavos(td)} CUP")
                    Text("Créditos: ${formatCentavos(tc)} CUP")
                    if (td != tc) {
                        Text(
                            "Diferencia: ${formatCentavos(abs(td - tc))} CUP",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (td > 0) {
                        Text("Cuadra ✓")
                    }
                    form.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item { Spacer(Modifier.padding(24.dp)) }
        }
    }

    lineaEligiendo?.let { id ->
        SelectorCuentaDialog(
            cuentas = cuentas,
            onElegir = { vm.elegirCuenta(id, it); lineaEligiendo = null },
            onCerrar = { lineaEligiendo = null }
        )
    }
}

@Composable
private fun LineaEditor(
    linea: LineaForm,
    puedeQuitar: Boolean,
    onElegirCuenta: () -> Unit,
    onCambio: (LineaForm) -> Unit,
    onQuitar: () -> Unit
) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onElegirCuenta, modifier = Modifier.weight(1f)) {
                    Text(
                        linea.cuenta?.let { "${it.codigo} ${it.nombre}" } ?: "Elegir cuenta",
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                if (puedeQuitar) {
                    IconButton(onClick = onQuitar) {
                        Icon(Icons.Default.Delete, contentDescription = "Quitar línea")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = linea.debito,
                    onValueChange = {
                        onCambio(linea.copy(debito = it, credito = if (it.isNotBlank()) "" else linea.credito))
                    },
                    label = { Text("Débito") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = linea.credito,
                    onValueChange = {
                        onCambio(linea.copy(credito = it, debito = if (it.isNotBlank()) "" else linea.debito))
                    },
                    label = { Text("Crédito") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = linea.descripcion,
                onValueChange = { onCambio(linea.copy(descripcion = it)) },
                label = { Text("Descripción (opcional)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun SelectorCuentaDialog(
    cuentas: List<CuentaEntity>,
    onElegir: (CuentaEntity) -> Unit,
    onCerrar: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    val filtradas = remember(busqueda, cuentas) {
        val q = busqueda.trim()
        if (q.isEmpty()) cuentas
        else cuentas.filter {
            it.codigo.contains(q, ignoreCase = true) || it.nombre.contains(q, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Elegir cuenta") },
        text = {
            Column {
                OutlinedTextField(
                    value = busqueda, onValueChange = { busqueda = it },
                    label = { Text("Buscar por código o nombre") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (cuentas.isEmpty()) {
                    Text(
                        "No hay cuentas que admitan movimientos. Carga el nomenclador en la pestaña Cuentas.",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    items(filtradas, key = { it.id }) { c ->
                        ListItem(
                            modifier = Modifier.clickable { onElegir(c) },
                            overlineContent = { Text(c.codigo) },
                            headlineContent = { Text(c.nombre) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
