/*
 * Responsabilidad: Pruebas unitarias del formato monetario utilizado por los precios de producto.
 */

package com.miguel.portomuinos.ui.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFormattingTest {

    // Formato monetario esperado por la interfaz.

    @Test
    fun `el precio utiliza coma decimal y símbolo de euro`() {

        val result =
            5.2.toPriceLabel()

        assertEquals(
            "5,20 €",
            result
        )
    }

    @Test
    fun `el precio mantiene dos decimales`() {

        val result =
            2.0.toPriceLabel()

        assertEquals(
            "2,00 €",
            result
        )
    }
}