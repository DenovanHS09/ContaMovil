package com.contamovil.core.database

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

/**
 * Respaldo y restauración de la base de datos completa.
 * El respaldo es una copia del archivo SQLite, que el usuario guarda donde quiera
 * mediante el selector de archivos del sistema (no necesita permisos de almacenamiento).
 */
@Singleton
class RespaldoManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase
) {
    private val prefs: SharedPreferences
        get() = context.getSharedPreferences("respaldo", Context.MODE_PRIVATE)

    /** Momento (ms) del último respaldo creado, o 0 si no hay ninguno. */
    fun ultimoRespaldo(): Long = prefs.getLong("ultimo", 0L)

    fun baseAbierta(): Boolean = db.isOpen

    /** Escribe una copia completa de la base de datos en [destino]. */
    suspend fun exportar(destino: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            vaciarWal()
            val origen = context.getDatabasePath(AppDatabase.NOMBRE)
            val copiados = context.contentResolver.openOutputStream(destino, "wt")?.use { salida ->
                origen.inputStream().use { it.copyTo(salida) }
            } ?: error("No se pudo abrir el archivo de destino")
            if (copiados != origen.length()) {
                error("La copia quedó incompleta. Inténtalo de nuevo.")
            }
            prefs.edit().putLong("ultimo", System.currentTimeMillis()).apply()
        }
    }

    /**
     * Sustituye la base de datos actual por la del respaldo [origen].
     * Antes valida el archivo y guarda una copia de los datos actuales en la app.
     * Si termina bien, la base queda cerrada: hay que reiniciar la app ([reiniciarApp]).
     */
    suspend fun restaurar(origen: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val temporal = File(context.cacheDir, "restaurar.db")
            try {
                context.contentResolver.openInputStream(origen)?.use { entrada ->
                    temporal.outputStream().use { entrada.copyTo(it) }
                } ?: error("No se pudo leer el archivo")
                validar(temporal)

                // Copia de seguridad de los datos actuales, por si acaso
                vaciarWal()
                val actual = context.getDatabasePath(AppDatabase.NOMBRE)
                val previo = File(context.filesDir, "respaldo-antes-de-restaurar.db")
                actual.copyTo(previo, overwrite = true)

                // Se prepara el reemplazo junto al archivo original para poder renombrar de golpe
                val nuevo = File(actual.parentFile, "restaurando.tmp")
                temporal.copyTo(nuevo, overwrite = true)

                db.close()
                File(actual.path + "-wal").delete()
                File(actual.path + "-shm").delete()
                if (!nuevo.renameTo(actual)) {
                    previo.copyTo(actual, overwrite = true)
                    nuevo.delete()
                    error("No se pudo reemplazar la base de datos. Tus datos se conservaron.")
                }
            } finally {
                temporal.delete()
            }
        }
    }

    /** Cierra la app y la vuelve a abrir, para cargar la base restaurada. */
    fun reiniciarApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
        exitProcess(0)
    }

    /** Pasa todo lo pendiente del registro WAL al archivo principal para que la copia sea completa. */
    private fun vaciarWal() {
        repeat(5) {
            val ocupado = db.openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(TRUNCATE)")
                .use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }
            if (ocupado == 0) return
            Thread.sleep(200)
        }
        error("No se pudo preparar la base de datos. Inténtalo de nuevo en unos segundos.")
    }

    /** Comprueba que [archivo] sea una base de datos de ContaMovil íntegra y no más nueva que la app. */
    private fun validar(archivo: File) {
        val cabecera = ByteArray(16)
        val leidos = archivo.inputStream().use { it.read(cabecera) }
        if (leidos < 16 || String(cabecera, Charsets.US_ASCII).trimEnd('\u0000') != "SQLite format 3") {
            error("El archivo no es un respaldo válido de ContaMovil")
        }

        val bd = SQLiteDatabase.openDatabase(archivo.path, null, SQLiteDatabase.OPEN_READWRITE)
        try {
            val version = bd.version
            if (version > AppDatabase.VERSION) {
                error("El respaldo es de una versión más nueva de la app. Actualiza la app primero.")
            }
            if (version < 1) error("El archivo no es un respaldo válido de ContaMovil")

            val tablas = mutableSetOf<String>()
            bd.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null).use { c ->
                while (c.moveToNext()) tablas.add(c.getString(0))
            }
            val requeridas = if (version >= 2) listOf("cuentas", "comprobantes", "asientos") else listOf("cuentas")
            if (!tablas.containsAll(requeridas)) {
                error("El archivo no es un respaldo válido de ContaMovil")
            }

            bd.rawQuery("PRAGMA integrity_check", null).use { c ->
                if (!c.moveToFirst() || c.getString(0) != "ok") error("El respaldo está dañado")
            }
        } finally {
            bd.close()
            File(archivo.path + "-wal").delete()
            File(archivo.path + "-shm").delete()
            File(archivo.path + "-journal").delete()
        }
    }
}
