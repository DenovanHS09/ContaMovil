package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.FilaBalance
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.Naturaleza

/** Una cuenta del árbol con sus importes acumulados (los suyos más los de todas sus descendientes). */
data class NodoSaldo(
    val id: Long,
    val codigo: String,
    val nombre: String,
    /** 1 = grupo, 2 = subgrupo, 3 = cuenta, 4 = subcuenta... */
    val nivel: Int,
    val naturaleza: Naturaleza,
    val esHoja: Boolean,
    val saldoInicial: Long,
    val debitos: Long,
    val creditos: Long
) {
    /** Saldo neto (débitos menos créditos): positivo = deudor, negativo = acreedor. */
    val saldoFinal: Long get() = saldoInicial + debitos - creditos

    val sinMovimiento: Boolean get() = saldoInicial == 0L && debitos == 0L && creditos == 0L

    /** Solo se marca en cuentas hoja con naturaleza definida: p. ej. una deudora con saldo acreedor. */
    val saldoContrario: Boolean
        get() = esHoja && (
            (naturaleza == Naturaleza.DEUDORA && saldoFinal < 0) ||
                (naturaleza == Naturaleza.ACREEDORA && saldoFinal > 0)
            )
}

data class SaldosPorNiveles(
    /** Árbol completo en orden (cada cuenta seguida de sus descendientes). */
    val nodos: List<NodoSaldo>,
    val totalDebitos: Long,
    val totalCreditos: Long,
    /** Suma de los saldos finales de los grupos: en una contabilidad cuadrada es siempre cero. */
    val sumaSaldos: Long,
    val nivelMaximo: Int,
    /** Filas del balance cuyo código no está en el plan (no debería ocurrir). */
    val sinUbicar: Int
) {
    val cuadra: Boolean get() = sumaSaldos == 0L && sinUbicar == 0
}

/** Acumula los saldos del balance hacia arriba por la jerarquía del plan de cuentas. */
object SaldosNiveles {

    fun construir(cuentas: List<CuentaEntity>, filas: List<FilaBalance>): SaldosPorNiveles {
        val porCodigo = filas.associateBy { it.codigo }
        val hijos = cuentas.groupBy { it.cuentaPadreId }
        val idsConocidos = cuentas.map { it.id }.toSet()
        val codigosConocidos = cuentas.map { it.codigo }.toSet()

        val nodos = mutableListOf<NodoSaldo>()
        val visitadas = mutableSetOf<Long>()

        /** Devuelve (inicial, débitos, créditos) acumulados de la cuenta y la agrega a [nodos] en pre-orden. */
        fun recorrer(c: CuentaEntity): Triple<Long, Long, Long> {
            if (!visitadas.add(c.id)) return Triple(0L, 0L, 0L)   // protege contra ciclos
            val lugar = nodos.size
            nodos.add(placeholder(c))
            val propia = porCodigo[c.codigo]
            var inicial = propia?.saldoInicial ?: 0L
            var deb = propia?.debitos ?: 0L
            var cred = propia?.creditos ?: 0L
            val lista = hijos[c.id].orEmpty().sortedBy { it.codigo }
            for (h in lista) {
                val (i, d, k) = recorrer(h)
                inicial += i; deb += d; cred += k
            }
            nodos[lugar] = NodoSaldo(
                id = c.id, codigo = c.codigo, nombre = c.nombre, nivel = c.nivel,
                naturaleza = c.naturaleza, esHoja = lista.isEmpty(),
                saldoInicial = inicial, debitos = deb, creditos = cred
            )
            return Triple(inicial, deb, cred)
        }

        // Raíces: sin madre o con una madre que no existe en el plan.
        val raices = cuentas
            .filter { it.cuentaPadreId == null || it.cuentaPadreId !in idsConocidos }
            .sortedBy { it.codigo }
        var sumaRaices = 0L
        for (r in raices) {
            val (i, d, k) = recorrer(r)
            sumaRaices += i + d - k
        }

        return SaldosPorNiveles(
            nodos = nodos,
            totalDebitos = filas.sumOf { it.debitos },
            totalCreditos = filas.sumOf { it.creditos },
            sumaSaldos = sumaRaices,
            nivelMaximo = nodos.maxOfOrNull { it.nivel } ?: 1,
            sinUbicar = filas.count { it.codigo !in codigosConocidos }
        )
    }

    private fun placeholder(c: CuentaEntity) = NodoSaldo(
        id = c.id, codigo = c.codigo, nombre = c.nombre, nivel = c.nivel,
        naturaleza = c.naturaleza, esHoja = true, saldoInicial = 0, debitos = 0, creditos = 0
    )
}

/** Nombre del nivel para mostrarlo en la interfaz. */
fun nombreNivel(nivel: Int): String = when (nivel) {
    1 -> "Grupos"
    2 -> "Subgrupos"
    3 -> "Cuentas"
    4 -> "Subcuentas"
    else -> "Nivel $nivel"
}
