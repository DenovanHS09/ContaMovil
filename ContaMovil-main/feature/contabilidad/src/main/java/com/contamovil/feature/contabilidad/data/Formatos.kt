package com.contamovil.feature.contabilidad.data

import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

val FORMATO_FECHA: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

/**
 * Convierte "1500.50" (o "1500,50") a centavos. Devuelve null si está vacío, no es un número,
 * es cero o negativo, o tiene más de dos decimales.
 */
fun parseCentavos(texto: String): Long? {
    val t = texto.trim().replace(',', '.')
    if (t.isEmpty()) return null
    val n = t.toBigDecimalOrNull() ?: return null
    if (n.signum() <= 0 || n.scale() > 2) return null
    return try {
        n.movePointRight(2).longValueExact()
    } catch (e: ArithmeticException) {
        null
    }
}

fun formatCentavos(centavos: Long): String =
    String.format(Locale.US, "%,d.%02d", centavos / 100, centavos % 100)

/** Saldo con su naturaleza: "D" = deudor, "A" = acreedor. */
fun formatSaldo(saldo: Long): String = when {
    saldo > 0 -> formatCentavos(saldo) + " D"
    saldo < 0 -> formatCentavos(-saldo) + " A"
    else -> "0.00"
}

/** Centavos a texto editable ("150050" -> "1500.50"), que [parseCentavos] vuelve a leer sin pérdida. */
fun centavosATexto(centavos: Long): String =
    "${centavos / 100}.${(centavos % 100).toString().padStart(2, '0')}"
