/*
 * Responsabilidad: Fila de producto utilizada en la tienda, con precio y acción rápida para añadir
 * al carrito.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.model.toPriceLabel
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoTextSecondary
import com.miguel.portomuinos.ui.theme.PortoPrimary

@Composable
fun StoreProductRow(
    product: ProductUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(172.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ProductListImage(
            product = product,
            modifier = Modifier
                .width(112.dp)
                .fillMaxHeight()
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = product.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = product.description,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 11.5.sp,
                lineHeight = 14.sp,
                color = PortoTextSecondary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = product.price.toPriceLabel(),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = PortoPrimary,
                            shape = CircleShape
                        )
                        .clickable(onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Añadir al carrito",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

// Utilidades internas del componente.

@Composable
private fun ProductListImage(
    product: ProductUi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CatalogImage(
            imageRes = product.imageRes,
            imageUrl = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}
