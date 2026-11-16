package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contamovil.core.database.entity.TipoEntidad
import com.contamovil.feature.contabilidad.data.normasAplicables
import com.contamovil.feature.contabilidad.data.nombreVisible
import com.contamovil.feature.contabilidad.data.nota
import com.contamovil.feature.contabilidad.data.textoVariante

/** Selector del tipo de entidad, con las normas relacionadas y la variante del patrimonio. */
@Composable
internal fun SelectorTipoEntidadDialog(
    titulo: String,
    explicacion: String,
    actual: TipoEntidad?,
    textoConfirmar: String,
    onElegir: (TipoEntidad) -> Unit,
    onCerrar: () -> Unit
) {
    var elegido by remember { mutableStateOf(actual) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(titulo) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(explicacion, style = MaterialTheme.typography.bodySmall)
                TipoEntidad.entries.forEach { t ->
                    Row(
                        Modifier.fillMaxWidth().clickable { elegido = t },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = elegido == t, onClick = { elegido = t })
                        Text(t.nombreVisible())
                    }
                }
                elegido?.let { t ->
                    Text(
                        "Patrimonio (grupo 30): variante ${t.textoVariante()}.",
                        style = MaterialTheme.typography.labelLarge
                    )
                    t.normasAplicables().forEach {
                        Text("• $it", style = MaterialTheme.typography.bodySmall)
                    }
                    t.nota()?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { elegido?.let(onElegir) }, enabled = elegido != null) {
                Text(textoConfirmar)
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
