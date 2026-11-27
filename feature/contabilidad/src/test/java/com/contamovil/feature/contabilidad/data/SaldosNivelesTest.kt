package com.contamovil.feature.contabilidad.data

import com.contamovil.core.database.dao.FilaBalance
import com.contamovil.core.database.entity.CuentaEntity
import com.contamovil.core.database.entity.TipoCuenta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaldosNivelesTest {

    private val cuentas = listOf(
        CuentaEntity(id = 1, codigo = "10", nombre = "ACTIVOS", tipo = TipoCuenta.ACTIVO, nivel = 1),
        CuentaEntity(id = 2, codigo = "10.1", nombre = "ACTIVOS CIRCULANTES", tipo = TipoCuenta.ACTIVO, nivel = 2, cuentaPadreId = 1),
        CuentaEntity(id = 3, codigo = "101", nombre = "Efectivo en Caja", tipo = TipoCuenta.ACTIVO, nivel = 3, cuentaPadreId = 2),
        CuentaEntity(id = 4, codigo = "109", nombre = "Efectivo en Banco", tipo = TipoCuenta.ACTIVO, nivel = 3, cuentaPadreId = 2),
        CuentaEntity(id = 5, codigo = "20", nombre = "PASIVOS", tipo = TipoCuenta.PASIVO, nivel = 1),
        CuentaEntity(id = 6, codigo = "405", nombre = "Cuentas por Pagar", tipo = TipoCuenta.PASIVO, nivel = 2, cuentaPadreId = 5)
    )

    private fun fila(codigo: String, inicial: Long, deb: Long, cred: Long) =
        FilaBalance(codigo, "x", inicial, deb, cred)

    private val filas = listOf(
        fila("101", 0, 10_000, 0),
        fila("109", 0, 0, 2_500),
        fila("405", 0, 0, 7_500)
    )

    @Test
    fun acumulaHaciaArribaYMantieneElOrden() {
        val r = SaldosNiveles.construir(cuentas, filas)
        assertEquals(listOf("10", "10.1", "101", "109", "20", "405"), r.nodos.map { it.codigo })
        val grupo10 = r.nodos.first { it.codigo == "10" }
        assertEquals(10_000L, grupo10.debitos)
        assertEquals(2_500L, grupo10.creditos)
        assertEquals(7_500L, grupo10.saldoFinal)
        assertEquals(grupo10.saldoFinal, r.nodos.first { it.codigo == "10.1" }.saldoFinal)
        assertEquals(-7_500L, r.nodos.first { it.codigo == "20" }.saldoFinal)
    }

    @Test
    fun laSumaDeLosGruposEsCeroSiLaContabilidadCuadra() {
        val r = SaldosNiveles.construir(cuentas, filas)
        assertEquals(0L, r.sumaSaldos)
        assertEquals(10_000L, r.totalDebitos)
        assertEquals(10_000L, r.totalCreditos)
        assertTrue(r.cuadra)
        assertEquals(3, r.nivelMaximo)
    }

    @Test
    fun distingueHojasYMarcaSaldoContrario() {
        val r = SaldosNiveles.construir(cuentas, filas)
        assertTrue(r.nodos.first { it.codigo == "101" }.esHoja)
        assertFalse(r.nodos.first { it.codigo == "10" }.esHoja)
        // 109 es deudora y quedó con saldo acreedor
        assertTrue(r.nodos.first { it.codigo == "109" }.saldoContrario)
        assertFalse(r.nodos.first { it.codigo == "101" }.saldoContrario)
        // los grupos nunca se marcan
        assertFalse(r.nodos.first { it.codigo == "10" }.saldoContrario)
    }

    @Test
    fun incluyeElSaldoInicialEnElAcumulado() {
        val r = SaldosNiveles.construir(
            cuentas,
            listOf(fila("101", 5_000, 1_000, 0), fila("405", -5_000, 0, 1_000))
        )
        val g10 = r.nodos.first { it.codigo == "10" }
        assertEquals(5_000L, g10.saldoInicial)
        assertEquals(6_000L, g10.saldoFinal)
        assertEquals(0L, r.sumaSaldos)
    }

    @Test
    fun cuentasSinMovimientoSeMarcanComoVacias() {
        val r = SaldosNiveles.construir(cuentas, listOf(fila("101", 0, 100, 0), fila("405", 0, 0, 100)))
        assertTrue(r.nodos.first { it.codigo == "109" }.sinMovimiento)
        assertFalse(r.nodos.first { it.codigo == "101" }.sinMovimiento)
    }

    @Test
    fun detectaFilasFueraDelPlanYLoSenalaEnElControl() {
        val r = SaldosNiveles.construir(cuentas, filas + fila("999X", 0, 500, 0))
        assertEquals(1, r.sinUbicar)
        assertFalse(r.cuadra)
    }

    @Test
    fun unPlanVacioNoFalla() {
        val r = SaldosNiveles.construir(emptyList(), emptyList())
        assertTrue(r.nodos.isEmpty())
        assertEquals(1, r.nivelMaximo)
    }

    @Test
    fun losNombresDeNivelSonLosEsperados() {
        assertEquals("Grupos", nombreNivel(1))
        assertEquals("Cuentas", nombreNivel(3))
        assertEquals("Nivel 5", nombreNivel(5))
    }
}
