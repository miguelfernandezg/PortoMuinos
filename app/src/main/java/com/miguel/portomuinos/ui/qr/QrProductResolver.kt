/*
 * Responsabilidad: Normaliza distintos formatos de QR y los resuelve contra los productos reales
 * del catálogo.
 */

package com.miguel.portomuinos.ui.qr

import android.net.Uri
import com.miguel.portomuinos.ui.model.ProductUi

object QrProductResolver {

    /*
     * Normaliza formatos de QR internos, deep links y URLs.
     * Los candidatos extraídos se comparan con el id y qr_code
     * de los productos cargados desde Supabase.
     */
    fun resolve(
        rawValue: String,
        products: List<ProductUi>
    ): ProductUi? {

        val raw =
            Uri.decode(
                rawValue.trim()
            )

        if (raw.isBlank()) {
            return null
        }

        val candidates =
            linkedSetOf<String>()

        candidates += raw

        candidates +=
            raw.substringAfterLast("/")
                .substringBefore("?")
                .substringBefore("#")

        runCatching {
            Uri.parse(raw)
        }.getOrNull()?.let { uri ->

            listOf(
                "productId",
                "product_id",
                "id",
                "product",
                "qr",
                "qrCode",
                "qr_code",
                "code"
            ).forEach { key ->

                uri.getQueryParameter(
                    key
                )?.let {
                    candidates += it
                }
            }

            uri.lastPathSegment
                ?.let {
                    candidates += it
                }
        }

        return products.firstOrNull { product ->

            candidates.any { candidate ->

                candidate.equals(
                    product.id,
                    ignoreCase = true
                ) ||
                        product.qrCode
                            ?.let { qrCode ->
                                candidate.equals(
                                    qrCode,
                                    ignoreCase = true
                                )
                            }
                        ?: false
            }
        }
    }
}
