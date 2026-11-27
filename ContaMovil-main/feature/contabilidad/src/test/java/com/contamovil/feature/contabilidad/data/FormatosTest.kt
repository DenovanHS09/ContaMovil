package com.contamovil.feature.contabilidad.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormatosTest {

    @Test
    fun parseCentavosAceptaImportesValidos() {
        assertEquals(150050L, parseCentavos("1500.50"))
        assertEquals(150050L, parseCentavos("1500,50"))
        assertEquals(100L, parseCentavos("1"))
        assertEquals(5L, parseCentavos("0.05"))
    }

    @Test
    fun parseCentavosRechazaImportesInvalidos() {
        assertNull(parseCentavos(""))
        assertNull(parseCentavos("0"))
        assertNull(parseCentavos("-5"))
        assertNull(parseCentavos("10.555"))
        assertNull(parseCentavos("abc"))
    }

    @Test
    fun centavosATextoYParseCentavosSonInversos() {
        for (c in listOf(1L, 5L, 99L, 100L, 150050L, 123456789L)) {
            assertEquals(c, parseCentavos(centavosATexto(c)))
        }
        assertEquals("0.05", centavosATexto(5))
        assertEquals("1500.50", centavosATexto(150050))
    }
}
