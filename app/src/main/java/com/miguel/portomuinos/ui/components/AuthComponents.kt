/*
 * Responsabilidad: Agrupa componentes visuales reutilizables de autenticación y onboarding para
 * mantener un diseño consistente.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                R.drawable.auth_background
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        content()
    }
}

@Composable
fun PortoMuinosLogo(
    modifier: Modifier = Modifier,
    showShadow: Boolean = true
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        /*
         * La sombra es una capa independiente.
         * El logo visible sigue siendo el VectorDrawable
         * importado desde el SVG de Figma.
         */
        if (showShadow) {
            Image(
                painter = painterResource(
                    R.drawable.logo_portomuinos_shadow
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = 1.dp,
                        y = 3.dp
                    )
            )
        }

        Image(
            painter = painterResource(
                R.drawable.logo_portomuinos
            ),
            contentDescription = "Porto-Muiños",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PortoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color =
                    if (enabled) {
                        PortoDarkGreen
                    } else {
                        PortoDarkGreen.copy(
                            alpha = 0.42f
                        )
                    },
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun PortoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = PortoDarkGreen,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.5.sp,
            color = PortoDarkGreen
        )
    }
}

@Composable
fun PortoSocialButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 19.dp,
    iconTint: Color = Color.Unspecified
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = PortoDarkGreen,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        /*
         * El icono y el texto se muestran como un único conjunto centrado.
         * Se mantiene una separación fija entre ambos para conservar
         * una alineación visual coherente entre los proveedores.
         */
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment =
                    Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    painter =
                        painterResource(iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier =
                        Modifier.size(iconSize)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )

            Text(
                text = text,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = PortoDarkGreen,
                modifier =
                    Modifier.width(160.dp)
            )
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIconRes: Int,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization =
        KeyboardCapitalization.None,
    containerColor: Color = PortoSurface
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFDCE5E2),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                leadingIconRes
            ),
            contentDescription = null,
            modifier = Modifier.size(21.dp)
        )

        Spacer(
            Modifier.width(11.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 13.5.sp,
                color = Color.Black
            ),
            singleLine = true,
            cursorBrush = SolidColor(
                PortoPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization
            ),
            visualTransformation =
                if (
                    isPassword &&
                    !passwordVisible
                ) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            decorationBox = { inner ->
                Box(
                    contentAlignment =
                        Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontFamily = Poppins,
                            fontWeight =
                                FontWeight.Normal,
                            fontSize = 13.5.sp,
                            color =
                                PortoTextSecondary
                        )
                    }

                    inner()
                }
            }
        )

        if (isPassword) {
            Spacer(
                Modifier.width(8.dp)
            )

            Image(
                painter = painterResource(
                    R.drawable.ic_auth_eye
                ),
                contentDescription =
                    if (passwordVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    },
                modifier = Modifier
                    .size(23.dp)
                    .clickable {
                        passwordVisible =
                            !passwordVisible
                    }
            )
        }
    }
}

@Composable
fun OnboardingDots(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(6.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(
                        if (
                            currentPage ==
                            index
                        ) {
                            10.dp
                        } else {
                            9.dp
                        }
                    )
                    .background(
                        color =
                            if (
                                currentPage ==
                                index
                            ) {
                                PortoPrimary
                            } else {
                                Color(
                                    0xFFD7DBDA
                                )
                            },
                        shape =
                            RoundedCornerShape(
                                50.dp
                            )
                    )
            )
        }
    }
}
