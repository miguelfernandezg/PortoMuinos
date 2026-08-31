/*
 * Responsabilidad: Muestra el acceso al carrito con su contador visual de productos activos.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoPrimary

@Composable
fun CartIconButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 31.dp,
    badgeSize: Dp = 19.dp
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            painter = painterResource(
                R.drawable.ic_cart
            ),
            contentDescription = "Carrito",
            tint = Color.Unspecified,
            modifier = Modifier.size(
                iconSize
            )
        )

        if (itemCount > 0) {

            val badgeText =
                if (itemCount > 9) {
                    "9+"
                } else {
                    itemCount.toString()
                }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = (-1).dp,
                        y = 1.dp
                    )
                    .size(badgeSize)
                    .background(
                        color = PortoPrimary,
                        shape = CircleShape
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = badgeText,
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 9.sp,
                    lineHeight = 9.sp,
                    color = Color.White
                )
            }
        }
    }
}