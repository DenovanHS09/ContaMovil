package com.contamovil

import android.app.Application
import com.contamovil.feature.contabilidad.data.NormativaRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ContaMovilApp : Application() {

    @Inject lateinit var normativa: NormativaRepository

    override fun onCreate() {
        super.onCreate()
        // Carga la normativa y completa las cuentas de la Res. 407/2019 (solo añade; es idempotente)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            runCatching { normativa.inicializar() }
        }
    }
}
