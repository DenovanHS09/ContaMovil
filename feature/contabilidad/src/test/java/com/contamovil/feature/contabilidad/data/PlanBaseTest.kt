package com.contamovil.feature.contabilidad.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanBaseTest {

    private val variantes = VarianteNomenclador.entries

    @Test
    fun codigosUnicosPorVariante() {
        for (v in variantes) {
            val codigos = PlanBase.cuentas(v).map { it.codigo }
            assertEquals("Códigos duplicados en $v", codigos.size, codigos.toSet().size)
        }
    }

    @Test
    fun todaCuentaConPadreTieneSuPadreEnElPlan() {
        for (v in variantes) {
            val plan = PlanBase.cuentas(v)
            val codigos = plan.map { it.codigo }.toSet()
            for (c in plan) {
                val padre = c.padre ?: continue
                assertTrue("${c.codigo} ($v) apunta a un padre inexistente: $padre", padre in codigos)
            }
        }
    }

    @Test
    fun cuentasDeLaResolucion407EstanCompletas() {
        val plan = PlanBase.cuentas(VarianteNomenclador.ESTATAL).associateBy { it.codigo }
        for (c in listOf("350", "576", "577", "578", "579", "580")) {
            assertTrue("Falta la cuenta $c", c in plan)
            for (sub in listOf("0010", "0020", "0030", "0040", "0050", "0060")) {
                assertTrue("Falta la subcuenta $c.$sub", "$c.$sub" in plan)
            }
        }
        assertEquals("Productores", plan.getValue("914.0010").nombre)
        assertEquals("Exportadores", plan.getValue("914.0020").nombre)
    }

    @Test
    fun normaDeClasificaElOrigenDeCadaCodigo() {
        assertEquals(NormativaBase.R407, NormativaBase.normaDe("350"))
        assertEquals(NormativaBase.R407, NormativaBase.normaDe("350.0010"))
        assertEquals(NormativaBase.R407, NormativaBase.normaDe("914.0010"))
        assertEquals(NormativaBase.R494, NormativaBase.normaDe("914"))
        assertEquals(NormativaBase.R494, NormativaBase.normaDe("101"))
    }

    @Test
    fun versionesBaseSinDuplicados() {
        val claves = NormativaBase.versiones().map {
            Triple(it.cuentaCodigo, it.variante, it.normaOrigenCodigo)
        }
        assertEquals(claves.size, claves.toSet().size)
    }

    @Test
    fun resumenesFase1ReferencianCuentasExistentes() {
        val codigos = PlanBase.cuentas(VarianteNomenclador.ESTATAL).map { it.codigo }.toSet()
        for (c in NormativaRevision.resumenesFase1.keys) {
            assertTrue("El resumen $c no corresponde a una cuenta del plan", c in codigos)
        }
    }

    @Test
    fun segurosNoSeMezclanConElPlanGeneral() {
        assertEquals(40, NormativaRevision.seguros.size)
        assertTrue(NormativaRevision.versionesSeguros().all { it.variante == "SEGUROS" })
    }
}
