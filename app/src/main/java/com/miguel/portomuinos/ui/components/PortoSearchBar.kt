/*
 * Responsabilidad: Campo de búsqueda reutilizable para catálogo, recetas y productos.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun PortoSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = PortoDarkGreen
        ),
        cursorBrush = SolidColor(PortoDarkGreen),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = PortoSurface,
                        shape = RoundedCornerShape(80.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = PortoTextSecondary
                )

                Spacer(Modifier.width(10.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = PortoTextSecondary
                        )
                    }

                    innerTextField()
                }

                Spacer(Modifier.width(16.dp))
            }
        }
    )
}
