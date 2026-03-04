package com.example.mccagent.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsCorrelationKeyFactoryTest {

    @Test
    fun `mantiene correlacion unica incluso con colision de hash conocida`() {
        val midA = "FB"
        val midB = "Ea"

        assertEquals(midA.hashCode(), midB.hashCode())

        val claveA = SmsCorrelationKeyFactory.claveCorrelacion(midA)
        val claveB = SmsCorrelationKeyFactory.claveCorrelacion(midB)

        assertNotEquals(claveA, claveB)
    }

    @Test
    fun `lote de mensajes con resultados mixtos no genera colisiones de correlacion`() {
        val mids = listOf("FB", "Ea", "mid-100", "mid-101", "mid-102")
        val claves = mids.map { SmsCorrelationKeyFactory.claveCorrelacion(it) }

        assertEquals(mids.size, claves.toSet().size)

        val resultados = mapOf(
            "FB" to "ENVIADO",
            "Ea" to "FALLIDO",
            "mid-100" to "ENVIADO",
            "mid-101" to "FALLIDO",
            "mid-102" to "ENVIADO",
        )

        val correlacion = resultados.mapKeys { (mid, _) -> SmsCorrelationKeyFactory.claveCorrelacion(mid) }
        assertEquals(resultados.size, correlacion.size)
        assertTrue(correlacion.containsValue("ENVIADO"))
        assertTrue(correlacion.containsValue("FALLIDO"))
    }
}
