package com.contamovil.feature.contabilidad.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/** Pantalla principal del módulo: pestañas Cuentas y Comprobantes. */
@Composable
fun ContabilidadScreen(vm: ComprobantesViewModel = hiltViewModel()) {
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var creando by rememberSaveable { mutableStateOf(false) }

    if (creando) {
        BackHandler { creando = false }
        NuevoComprobanteScreen(vm = vm, onTerminar = { creando = false })
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = { pestana = 0 },
                        icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                        label = { Text("Cuentas") }
                    )
                    NavigationBarItem(
                        selected = pestana == 1,
                        onClick = { pestana = 1 },
                        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        label = { Text("Comprobantes") }
                    )
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                if (pestana == 0) {
                    PlanCuentasScreen()
                } else {
                    ComprobantesScreen(vm = vm, onNuevo = { vm.nuevo(); creando = true })
                }
            }
        }
    }
}
