package com.contamovil.feature.contabilidad.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val SIETE_DIAS_MS = 7L * 24 * 60 * 60 * 1000

private fun textoUltimoRespaldo(ms: Long): String =
    if (ms == 0L) {
        "Último respaldo: nunca. ¡Crea uno cuanto antes!"
    } else {
        "Último respaldo: " + Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AjustesContenido(onVerNormativa: () -> Unit, vm: AjustesViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    val crearArchivo = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> if (uri != null) vm.exportar(uri) }

    val abrirArchivo = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) vm.pedirRestaurar(uri) }

    val desactualizado = ui.ultimoRespaldo == 0L ||
        System.currentTimeMillis() - ui.ultimoRespaldo > SIETE_DIAS_MS

    Scaffold(topBar = { TopAppBar(title = { Text("Ajustes") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Respaldo de datos", style = MaterialTheme.typography.titleMedium)
            Text(
                "El respaldo es un archivo con todas tus cuentas y comprobantes. " +
                    "Guárdalo fuera del teléfono (Drive, correo u otra memoria) para no perderlo " +
                    "si cambias de equipo o desinstalas la app."
            )
            Text(
                textoUltimoRespaldo(ui.ultimoRespaldo),
                color = if (desactualizado) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = { crearArchivo.launch("contamovil-respaldo-${LocalDate.now()}.db") },
                enabled = !ui.trabajando,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear respaldo") }
            OutlinedButton(
                onClick = { abrirArchivo.launch(arrayOf("*/*")) },
                enabled = !ui.trabajando,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Restaurar respaldo") }
            if (ui.trabajando) CircularProgressIndicator()
            ui.mensaje?.let { Text(it) }

            Text("Normativa", style = MaterialTheme.typography.titleMedium)
            Text(
                "Consulta las normas que sustentan el nomenclador, qué cambió cada una y el historial de cada cuenta."
            )
            OutlinedButton(onClick = onVerNormativa, modifier = Modifier.fillMaxWidth()) {
                Text("Normativa del nomenclador")
            }
        }
    }

    ui.pendiente?.let {
        AlertDialog(
            onDismissRequest = { vm.cancelarRestaurar() },
            title = { Text("¿Restaurar respaldo?") },
            text = {
                Text(
                    "Se reemplazarán TODOS los datos actuales (cuentas y comprobantes) por los del " +
                        "respaldo. Antes se guardará una copia de tus datos actuales dentro de la app. " +
                        "Después la app se reiniciará."
                )
            },
            confirmButton = { TextButton(onClick = { vm.confirmarRestaurar() }) { Text("Restaurar") } },
            dismissButton = { TextButton(onClick = { vm.cancelarRestaurar() }) { Text("Cancelar") } }
        )
    }

    ui.reinicio?.let { mensaje ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Reinicio necesario") },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = { vm.reiniciar() }) { Text("Reiniciar app") } }
        )
    }
}

@Composable
fun AjustesScreen() {
    var verNormativa by rememberSaveable { mutableStateOf(false) }
    if (verNormativa) {
        BackHandler { verNormativa = false }
        NormativaScreen(onVolver = { verNormativa = false })
    } else {
        AjustesContenido(onVerNormativa = { verNormativa = true })
    }
}
