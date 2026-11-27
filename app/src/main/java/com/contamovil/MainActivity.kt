package com.contamovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.contamovil.feature.contabilidad.ui.ContabilidadScreen
import dagger.hilt.android.AndroidEntryPoint

private val ContaMovilColors = lightColorScheme(
    primary = Color(0xFF168C89),
    onPrimary = Color.White,
    secondary = Color(0xFF071D3A),
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFFF5F6F4),
    onBackground = Color(0xFF17212B),
    surface = Color.White,
    onSurface = Color(0xFF17212B),
    surfaceVariant = Color(0xFFE9EFEE),
    onSurfaceVariant = Color(0xFF52616B)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = ContaMovilColors) {
                ContabilidadScreen()
            }
        }
    }
}
