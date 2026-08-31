/*
 * Responsabilidad: Pantalla de carrito con cantidades, total estimado, vaciado y salida controlada
 * hacia la tienda oficial.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.CatalogImage
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun CartScreen(
    products: List<ProductUi>,
    quantities: Map<String, Int>,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearCart: () -> Unit,
    onContinueShopping: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    // Datos derivados del carrito y total estimado.

    val cartProducts =
        products.filter { product ->
            (quantities[product.id] ?: 0) > 0
        }

    val total =
        cartProducts.sumOf { product ->
            product.price *
                    (quantities[product.id] ?: 0)
        }

    // Estado visual de los diálogos de confirmación.

    var showClearCartDialog by remember {
        mutableStateOf(false)
    }

    var showCheckoutDialog by remember {
        mutableStateOf(false)
    }

    // Contenido principal del carrito.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
    ) {

        CartHeader(
            showClearAction =
                cartProducts.isNotEmpty(),
            onBackClick = onBackClick,
            onClearClick = {
                showClearCartDialog = true
            }
        )

        if (cartProducts.isEmpty()) {

            EmptyCartContent(
                modifier = Modifier.weight(1f),
                onContinueShopping =
                    onContinueShopping
            )

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding =
                    PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 6.dp,
                        bottom = 12.dp
                    )
            ) {

                items(
                    items = cartProducts,
                    key = { it.id }
                ) { product ->

                    CartProductRow(
                        product = product,
                        quantity =
                            quantities[
                                product.id
                            ] ?: 1,
                        onClick = {
                            onProductClick(
                                product.id
                            )
                        },
                        onIncrease = {
                            onIncrease(
                                product.id
                            )
                        },
                        onDecrease = {
                            onDecrease(
                                product.id
                            )
                        },
                        onRemove = {
                            onRemove(
                                product.id
                            )
                        }
                    )

                    HorizontalDivider(
                        color =
                            PortoTextSecondary.copy(
                                alpha = 0.13f
                            )
                    )
                }
            }

            CartSummary(
                total = total,
                onCheckoutClick = {
                    showCheckoutDialog =
                        true
                }
            )
        }
    }

    // Diálogo de vaciado completo.

    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearCartDialog = false
            },
            title = {
                Text(
                    text = "¿Vaciar carrito?",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text =
                        "Se eliminarán todos los productos del carrito.",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Normal,
                    color =
                        PortoTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCart()
                        showClearCartDialog = false
                    }
                ) {
                    Text(
                        text = "Vaciar",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            Color(0xFFC94B4B)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearCartDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.Medium,
                        color =
                            PortoDarkGreen
                    )
                }
            },
            containerColor = Color.White
        )
    }

    // Diálogo previo a abandonar la app y continuar en la tienda oficial.

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showCheckoutDialog = false
            },
            title = {
                Text(
                    text =
                        "Continuar en Porto-Muiños",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text =
                        "La compra se completará en la tienda oficial de Porto-Muiños. Tu selección seguirá guardada en la app.",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Normal,
                    color =
                        PortoTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCheckoutDialog =
                            false

                        onCheckoutClick()
                    }
                ) {
                    Text(
                        text =
                            "Ir a la tienda",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            PortoDarkGreen
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCheckoutDialog =
                            false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.Medium,
                        color =
                            PortoTextSecondary
                    )
                }
            },
            containerColor =
                Color.White
        )
    }
}

// Componentes internos de la pantalla.

@Composable
private fun CartHeader(
    showClearAction: Boolean,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .padding(
                horizontal = 14.dp
            )
    ) {

        Box(
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .size(44.dp)
                .clickable(
                    onClick = onBackClick
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Icon(
                imageVector =
                    Icons.Outlined.ArrowBack,
                contentDescription =
                    "Volver",
                tint =
                    PortoDarkGreen,
                modifier =
                    Modifier.size(23.dp)
            )
        }

        Text(
            text = "Tu carrito",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 25.sp,
            color = Color.Black,
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(
                    start = 46.dp
                )
        )

        if (showClearAction) {
            Text(
                text = "Vaciar",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 12.5.sp,
                color =
                    PortoTextSecondary,
                modifier = Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .clickable(
                        onClick =
                            onClearClick
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 10.dp
                    )
            )
        }
    }
}

@Composable
private fun CartProductRow(
    product: ProductUi,
    quantity: Int,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 14.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(94.dp)
                .clip(
                    RoundedCornerShape(
                        15.dp
                    )
                )
                .background(
                    PortoSurface
                )
                .clickable(
                    onClick = onClick
                ),
            contentAlignment =
                Alignment.Center
        ) {

            CatalogImage(
                imageRes = product.imageRes,
                imageUrl = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }

        Spacer(
            Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = product.name,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                color = Color.Black,
                modifier =
                    Modifier.clickable(
                        onClick = onClick
                    )
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(
                text =
                    product.price
                        .toPriceLabel(),
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 13.sp,
                color =
                    PortoPrimary
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                QuantityControlButton(
                    text = "−",
                    onClick =
                        onDecrease
                )

                Text(
                    text =
                        quantity.toString(),
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color =
                        PortoDarkGreen,
                    modifier =
                        Modifier.padding(
                            horizontal = 13.dp
                        )
                )

                QuantityControlButton(
                    text = "+",
                    onClick =
                        onIncrease
                )

                Spacer(
                    Modifier.width(12.dp)
                )

                Text(
                    text = "Eliminar",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Medium,
                    fontSize = 11.sp,
                    color =
                        PortoTextSecondary,
                    modifier =
                        Modifier.clickable(
                            onClick =
                                onRemove
                        )
                )
            }
        }
    }
}

@Composable
private fun QuantityControlButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(
                color =
                    PortoSurface,
                shape =
                    CircleShape
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 17.sp,
            color =
                PortoDarkGreen
        )
    }
}

@Composable
private fun CartSummary(
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White
            )
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                /*
                 * Reserva espacio adicional para que el resumen y el CTA
                 * queden claramente separados de la navegación inferior.
                 */
                bottom =
                    PortoBottomBarOverlayClearance +
                            20.dp
            )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "Total estimado",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 14.sp,
                color =
                    PortoTextSecondary
            )

            Text(
                text =
                    total.toPriceLabel(),
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 20.sp,
                color =
                    PortoDarkGreen
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    color =
                        PortoDarkGreen,
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .clickable(
                    onClick =
                        onCheckoutClick
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Text(
                text =
                    "Continuar en Porto-Muiños",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                color =
                    Color.White
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            text =
                "El pago y el envío se completan en la tienda oficial.",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color =
                PortoTextSecondary,
            textAlign =
                TextAlign.Center,
            modifier =
                Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmptyCartContent(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 34.dp
            )
            .padding(
                bottom =
                    PortoBottomBarOverlayClearance +
                            12.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Image(
            painter = painterResource(
                R.drawable.mascota_carrito
            ),
            contentDescription =
                "AlgaeChef con un carrito vacío",
            contentScale =
                ContentScale.Fit,
            modifier = Modifier.size(162.dp)
        )

        Spacer(
            Modifier.height(14.dp)
        )

        Text(
            text =
                "Tu carrito está vacío",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 20.sp,
            color =
                Color.Black
        )

        Spacer(
            Modifier.height(6.dp)
        )

        Text(
            text =
                "Descubre productos Porto-Muiños y añade los que quieras comprar.",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color =
                PortoTextSecondary,
            textAlign =
                TextAlign.Center
        )

        Spacer(
            Modifier.height(22.dp)
        )

        Box(
            modifier = Modifier
                .background(
                    color =
                        PortoMint,
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .clickable(
                    onClick =
                        onContinueShopping
                )
                .padding(
                    horizontal = 25.dp,
                    vertical = 12.dp
                )
        ) {
            Text(
                text =
                    "Ir a la tienda",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 13.sp,
                color =
                    PortoDarkGreen
            )
        }
    }
}