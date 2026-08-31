/*
 * Responsabilidad: Cabecera principal de Home con acceso al perfil y al carrito del usuario.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface

@Composable
fun HomeTopBar(
    userName: String,
    cartItemCount: Int,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    profileImageUri: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        /*
         * Avatar + saludo funcionan como una única zona pulsable
         * que da acceso al perfil del usuario.
         */
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    onClick = onProfileClick
                )
                .padding(
                    vertical = 3.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfileAvatar(
                imageUri = profileImageUri,
                size = 44.dp,
                placeholderIconSize = 31.dp,
                backgroundColor = PortoSurface
            )

            Spacer(
                Modifier.width(10.dp)
            )

            Column {
                Text(
                    text = "¡Hola $userName!",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    color = Color.Black
                )

                Text(
                    text = "¿Qué cocinamos hoy?",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    color = Color.Black
                )
            }
        }

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(2.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            /*
             * Acceso rápido a la colección. Siempre outline:
             * no representa el estado favorito de Home.
             */
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clickable(
                        onClick =
                            onFavoritesClick
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.FavoriteBorder,
                    contentDescription =
                        "Mis favoritos",
                    tint =
                        PortoDarkGreen,
                    modifier =
                        Modifier.size(27.dp)
                )
            }

            CartIconButton(
                itemCount = cartItemCount,
                onClick = onCartClick
            )
        }
    }
}
