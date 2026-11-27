package com.contamovil.feature.contabilidad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.feature.contabilidad.data.formatCentavos
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Navy = Color(0xFF071D3A)
private val Teal = Color(0xFF168C89)
private val Muted = Color(0xFF667581)

@Composable
fun InicioScreen(
    vm: ComprobantesViewModel,
    onCuentas: () -> Unit,
    onNuevo: () -> Unit,
    onInformes: () -> Unit
) {
    val comprobantes by vm.comprobantes.collectAsStateWithLifecycle()
    val fecha = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))
    ).replaceFirstChar { it.uppercase(Locale("es")) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F6F4)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("CONTAMOVIL", color = Teal, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Tu contabilidad,\nbajo control", color = Navy, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(fecha, color = Muted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Navy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("COMPROBANTES REGISTRADOS", color = Color(0xFFB8D6D4), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(comprobantes.size.toString(), color = Color.White, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    Text("Asientos contables guardados", color = Color(0xFFD7E1E8), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Text("Accesos rápidos", color = Navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction("Plan de\ncuentas", Modifier.weight(1f), onCuentas)
                QuickAction("Nuevo\ncomprobante", Modifier.weight(1f), onNuevo)
                QuickAction("Informes", Modifier.weight(1f), onInformes)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Actividad reciente", color = Navy, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Últimos 5", color = Muted, style = MaterialTheme.typography.labelMedium)
            }
        }
        if (comprobantes.isEmpty()) {
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Todavía no hay comprobantes", color = Navy, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Cuando registres uno, aparecerá aquí.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(comprobantes.take(5), key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(Modifier.padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text("Comprobante N.º ${item.numero}", color = Teal, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Text(item.concepto, color = Navy, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(LocalDate.ofEpochDay(item.fecha).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${formatCentavos(item.total)} CUP", color = Navy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAction(label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(vertical = 16.dp, horizontal = 9.dp)) {
            Text("↗", color = Teal, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(label, color = Navy, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
