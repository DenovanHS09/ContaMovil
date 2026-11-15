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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contamovil.feature.contabilidad.data.FORMATO_FECHA
import com.contamovil.feature.contabilidad.data.formatCentavos
import com.contamovil.feature.contabilidad.data.formatSaldo
import java.time.LocalDate

private fun fechaCorta(epochDia: Long): String =
    LocalDate.ofEpochDay(epochDia).format(FORMATO_FECHA)

/** Informes: libro diario, libro mayor y balance de comprobación (CUP). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformesScreen(vm: InformesViewModel = hiltViewModel()) {
    var vista by rememberSaveable { mutableIntStateOf(0) }
    val desde by vm.desde.collectAsStateWithLifecycle()
    val hasta by vm.hasta.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val cuentaMayor by vm.cuentaMayor.collectAsStateWithLifecycle()

    LaunchedEffect(vista, cuentaMayor) { vm.actualizar(vista) }

    Scaffold(topBar = { TopAppBar(title = { Text("Informes") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Diario", "Mayor", "Balance").forEachIndexed { i, titulo ->
                    FilterChip(
                        selected = vista == i,
                        onClick = { vista = i },
                        label = { Text(titulo) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = desde, onValueChange = vm::setDesde,
                    label = { Text("Desde") }, singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hasta, onValueChange = vm::setHasta,
                    label = { Text("Hasta") }, singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Button(onClick = { vm.actualizar(vista) }, modifier = Modifier.fillMaxWidth()) {
                Text("Consultar (dd/mm/aaaa)")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            when (vista) {
                0 -> DiarioVista(vm)
                1 -> MayorVista(vm)
                else -> BalanceVista(vm)
            }
        }
    }
}

@Composable
private fun DiarioVista(vm: InformesViewModel) {
    val lineas by vm.diario.collectAsStateWithLifecycle()
    if (lineas.isEmpty()) {
        Text("No hay movimientos en el período.")
    } else {
        val grupos = remember(lineas) { lineas.groupBy { it.comprobanteId }.values.toList() }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(grupos, key = { it.first().comprobanteId }) { grupo ->
                val cab = grupo.first()
                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(
                        "N.º ${cab.numero} · ${fechaCorta(cab.fecha)}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(cab.concepto, style = MaterialTheme.typography.titleSmall)
                    grupo.forEach { l ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${l.codigo} ${l.cuenta}", Modifier.weight(1f))
                            Text(
                                if (l.debito > 0) "D ${formatCentavos(l.debito)}"
                                else "C ${formatCentavos(l.credito)}"
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(top = 6.dp))
                }
            }
            item {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text("Total débitos: ${formatCentavos(lineas.sumOf { it.debito })} CUP")
                    Text("Total créditos: ${formatCentavos(lineas.sumOf { it.credito })} CUP")
                }
            }
        }
    }
}

@Composable
private fun MayorVista(vm: InformesViewModel) {
    val mayor by vm.mayor.collectAsStateWithLifecycle()
    val cuentas by vm.cuentas.collectAsStateWithLifecycle()
    val cuenta by vm.cuentaMayor.collectAsStateWithLifecycle()
    var eligiendo by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { eligiendo = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                cuenta?.let { "${it.codigo} ${it.nombre}" } ?: "Elegir cuenta",
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        val m = mayor
        if (m == null) {
            Text("Elige una cuenta para ver su libro mayor.")
        } else {
            Text("Saldo anterior: ${formatSaldo(m.saldoAnterior)}   (D = deudor, A = acreedor)")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(m.filas) { f ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            "${fechaCorta(f.linea.fecha)} · N.º ${f.linea.numero} · ${f.linea.concepto}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (f.linea.debito > 0) "D ${formatCentavos(f.linea.debito)}"
                                else "C ${formatCentavos(f.linea.credito)}",
                                Modifier.weight(1f)
                            )
                            Text("Saldo ${formatSaldo(f.saldo)}")
                        }
                        HorizontalDivider(Modifier.padding(top = 4.dp))
                    }
                }
                item {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Text("Débitos del período: ${formatCentavos(m.totalDebitos)} CUP")
                        Text("Créditos del período: ${formatCentavos(m.totalCreditos)} CUP")
                        Text("Saldo final: ${formatSaldo(m.saldoFinal)}")
                    }
                }
            }
        }
    }

    if (eligiendo) {
        SelectorCuentaDialog(
            cuentas = cuentas,
            onElegir = { vm.elegirCuentaMayor(it); eligiendo = false },
            onCerrar = { eligiendo = false }
        )
    }
}

@Composable
private fun BalanceVista(vm: InformesViewModel) {
    val filas by vm.balance.collectAsStateWithLifecycle()
    if (filas.isEmpty()) {
        Text("No hay movimientos hasta esa fecha.")
    } else {
        val saldoFinal = { f: com.contamovil.core.database.dao.FilaBalance ->
            f.saldoInicial + f.debitos - f.creditos
        }
        val totalDebitos = filas.sumOf { it.debitos }
        val totalCreditos = filas.sumOf { it.creditos }
        val saldosDeudores = filas.sumOf { maxOf(0L, saldoFinal(it)) }
        val saldosAcreedores = filas.sumOf { maxOf(0L, -saldoFinal(it)) }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(filas, key = { it.codigo }) { f ->
                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${f.codigo} ${f.cuenta}", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Inicial ${formatSaldo(f.saldoInicial)} · Débitos ${formatCentavos(f.debitos)} · " +
                            "Créditos ${formatCentavos(f.creditos)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("Saldo final: ${formatSaldo(saldoFinal(f))}")
                    HorizontalDivider(Modifier.padding(top = 4.dp))
                }
            }
            item {
                Column(Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Débitos del período: ${formatCentavos(totalDebitos)} CUP")
                    Text("Créditos del período: ${formatCentavos(totalCreditos)} CUP")
                    Text("Saldos deudores: ${formatCentavos(saldosDeudores)} CUP")
                    Text("Saldos acreedores: ${formatCentavos(saldosAcreedores)} CUP")
                    if (saldosDeudores == saldosAcreedores) {
                        Text("Balance cuadrado ✓")
                    } else {
                        Text(
                            "El balance no cuadra",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
