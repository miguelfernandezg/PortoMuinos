/*
 * Responsabilidad: Tarjeta compacta utilizada para destacar productos en la pantalla de inicio.
 */

package com.miguel.portomuinos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

private val ProductCardShape = RoundedCornerShape(15.dp)

@Composable
fun FeaturedProductCard(
    @DrawableRes imageRes: Int?,
    name: String,
    price: String?,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(150.dp)
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(ProductCardShape)
                .background(PortoSurface),
            contentAlignment = Alignment.Center
        ) {

            CatalogImage(
                imageRes = imageRes,
                imageUrl = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Text(
            text = name,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.5.sp,
            lineHeight = 17.sp,
            color = PortoDarkGreen
        )

        if (!price.isNullOrBlank()) {

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = price,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = PortoTextSecondary
            )
        }
    }
}