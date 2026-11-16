package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import java.time.LocalDate
import java.time.format.DateTimeParseException

private fun etiqueta(nombre: String): String = nombre.lowercase().replace('_', ' ')

private fun fechaUi(iso: String?, vacio: String = "no verificada"): String =
    if (iso == null) {
        vacio
    } else {
        try {
            LocalDate.parse(iso).format(FORMATO_FECHA)
        } catch (e: DateTimeParseException) {
            iso
        }
    }

/** Consulta de solo lectura de la normativa del nomenclador y del historial de cada cuenta. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormativaScreen(onVolver: () -> Unit, vm: NormativaViewModel = hiltViewModel()) {
    var vista by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Normativa del nomenclador") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Normas", "Cambios", "Cuenta").forEachIndexed { i, titulo ->
                    FilterChip(selected = vista == i, onClick = { vista = i }, label = { Text(titulo) })
                }
            }
            when (vista) {
                0 -> NormasVista(vm)
                1 -> CambiosVista(vm)
                else -> CuentaVista(vm)
            }
        }
    }
}

@Composable
private fun NormasVista(vm: NormativaViewModel) {
    val normas by vm.normas.collectAsStateWithLifecycle()
    if (normas.isEmpty()) {
        Text("Cargando normativa…")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(normas, key = { it.id }) { n ->
                val fuente = n.gaceta + (n.identificador?.let { " ($it)" } ?: "")
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Res. ${n.numero}/${n.anio} · ${etiqueta(n.estado.name)}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(n.titulo)
                        Text("Instrumento: ${etiqueta(n.instrumento.name)}", style = MaterialTheme.typography.bodySmall)
                        Text("Fuente: $fuente", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Firma: ${fechaUi(n.fechaFirma)} · Publicación: ${fechaUi(n.fechaPublicacion)} · " +
                                "Vigencia: ${fechaUi(n.fechaVigencia)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        n.derogadaPorCodigo?.let {
                            Text("Derogada por: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            "Verificación: ${etiqueta(n.verificacion.name)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        n.observaciones?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CambiosVista(vm: NormativaViewModel) {
    val cambios by vm.cambios.collectAsStateWithLifecycle()
    if (cambios.isEmpty()) {
        Text("Cargando cambios…")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cambios, key = { it.id }) { c ->
                val pendiente = c.estado.name.startsWith("PENDIENTE")
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "${c.normaCodigo} · cuenta ${c.cuentaCodigo}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(etiqueta(c.tipoCambio.name))
                        Text(
                            "Estado: ${etiqueta(c.estado.name)}",
                            color = if (pendiente) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                        c.valorAnterior?.let { Text("Antes: $it", style = MaterialTheme.typography.bodySmall) }
                        c.valorNuevo?.let { Text("Después: $it", style = MaterialTheme.typography.bodySmall) }
                        c.observaciones?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CuentaVista(vm: NormativaViewModel) {
    val versiones by vm.versiones.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var codigo by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = codigo, onValueChange = { codigo = it },
                label = { Text("Código") }, singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fecha, onValueChange = { fecha = it },
                label = { Text("Vigente al (opcional)") }, singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Button(onClick = { vm.consultar(codigo, fecha) }, modifier = Modifier.fillMaxWidth()) {
            Text("Consultar (fecha dd/mm/aaaa)")
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        val lista = versiones
        if (lista != null && lista.isEmpty()) {
            Text("No hay versiones registradas para esa cuenta en esa fecha.")
        } else if (lista != null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista, key = { it.id }) { v ->
                    val pendiente = v.estadoNormativo.name.startsWith("PENDIENTE")
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "${v.cuentaCodigo} · ${v.denominacion}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            val variante = v.variante?.let { " · ${etiqueta(it)}" } ?: ""
                            Text(
                                "${etiqueta(v.naturaleza.name)} · grupo ${v.grupo} · subgrupo ${v.subgrupo}$variante",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Norma: ${v.normaOrigenCodigo} · desde ${fechaUi(v.vigenteDesde, "origen")} " +
                                    "hasta ${fechaUi(v.vigenteHasta, "actual")}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Estado: ${etiqueta(v.estadoNormativo.name)}",
                                color = if (pendiente) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurface
                            )
                            v.usoContenido?.let { Text("Uso: $it", style = MaterialTheme.typography.bodySmall) }
                            v.funcionamientoDebitos?.let { Text("Débitos: $it", style = MaterialTheme.typography.bodySmall) }
                            v.funcionamientoCreditos?.let { Text("Créditos: $it", style = MaterialTheme.typography.bodySmall) }
                            v.observaciones?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
