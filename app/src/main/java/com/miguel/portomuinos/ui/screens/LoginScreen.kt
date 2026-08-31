/*
 * Responsabilidad: Pantalla de inicio de sesión por correo y acceso a proveedores sociales
 * disponibles.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.AuthTextField
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.components.PortoMuinosLogo
import com.miguel.portomuinos.ui.components.PortoPrimaryButton
import com.miguel.portomuinos.ui.components.PortoSocialButton
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun LoginScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    authErrorText: String? = null,
    infoText: String? = null,
    isLoading: Boolean = false,
    onAuthMessageClear: () -> Unit = {},
    onLoginClick:
        (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSocialLoginClick: (String) -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var errorText by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 17.dp
            )
    ) {

        Spacer(
            Modifier.height(8.dp)
        )

        /*
         * Se reserva el hueco aunque la flecha no se muestre.
         * Así el logo no cambia de posición entre accesos.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            if (showBackButton) {
                PortoBackButton(
                    onClick = onBackClick,
                    style = PortoBackButtonStyle.PLAIN,
                    size = 44.dp,
                    iconSize = 23.dp
                )
            }
        }

        PortoMuinosLogo(
            modifier = Modifier
                .align(
                    Alignment.CenterHorizontally
                )
                .size(
                    width = 224.dp,
                    height = 109.dp
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 30.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 46.dp
                    )
                    .shadow(
                        elevation = 3.dp,
                        shape =
                            RoundedCornerShape(
                                19.dp
                            )
                    )
                    .background(
                        color = PortoSurface,
                        shape =
                            RoundedCornerShape(
                                19.dp
                            )
                    )
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 20.dp,
                        bottom = 16.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "Bienvenido de nuevo",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Inicia sesión para continuar",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Normal,
                    fontSize = 12.5.sp,
                    color =
                        PortoTextSecondary
                )

                Spacer(
                    Modifier.height(18.dp)
                )

                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorText = null
                        onAuthMessageClear()
                    },
                    placeholder =
                        "Correo electrónico",
                    leadingIconRes =
                        R.drawable.ic_auth_email,
                    keyboardType =
                        KeyboardType.Email,
                    containerColor =
                        Color.White
                )

                Spacer(
                    Modifier.height(11.dp)
                )

                AuthTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorText = null
                        onAuthMessageClear()
                    },
                    placeholder =
                        "Contraseña",
                    leadingIconRes =
                        R.drawable.ic_auth_lock,
                    isPassword = true,
                    keyboardType =
                        KeyboardType.Password,
                    containerColor =
                        Color.White
                )

                val visibleErrorText =
                    errorText ?: authErrorText

                if (visibleErrorText != null) {

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        text = visibleErrorText,
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.Normal,
                        fontSize = 11.5.sp,
                        color =
                            Color(0xFFC94B4B),
                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }

                if (
                    infoText != null &&
                    visibleErrorText == null
                ) {

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color =
                                    PortoMint.copy(
                                        alpha = 0.40f
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    )
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            )
                    ) {

                        Text(
                            text = infoText,
                            fontFamily = Poppins,
                            fontWeight =
                                FontWeight.Medium,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = PortoPrimary
                        )
                    }
                }

                Spacer(
                    Modifier.height(10.dp)
                )

                Text(
                    text =
                        "¿Has olvidado tu contraseña?",
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = PortoPrimary,
                    modifier = Modifier
                        .align(
                            Alignment.End
                        )
                        .clickable(
                            onClick =
                                onForgotPasswordClick
                        )
                        .padding(
                            vertical = 4.dp
                        )
                )
            }

            /*
             * Asset recortado de Figma para el Login.
             * Se dibuja después de la card para quedar por encima,
             * como en el diseño original.
             */
            Image(
                painter = painterResource(
                    R.drawable.mascota_saludando_login
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = (-12).dp,
                        y = (-28).dp
                    )
                    .requiredSize(
                        width = 92.dp,
                        height = 89.dp
                    )
            )

        }

        Spacer(
            Modifier.height(38.dp)
        )

        PortoPrimaryButton(
            text =
                if (isLoading) {
                    "Iniciando sesión..."
                } else {
                    "Iniciar sesión"
                },
            enabled = !isLoading,
            onClick = {
                when {
                    email.isBlank() ->
                        errorText =
                            "Introduce tu correo electrónico."

                    !email.contains("@") ->
                        errorText =
                            "Introduce un correo electrónico válido."

                    password.length < 4 ->
                        errorText =
                            "Introduce tu contraseña."

                    else ->
                        onLoginClick(
                            email.trim(),
                            password
                        )
                }
            }
        )

        Spacer(
            Modifier.height(10.dp)
        )

        PortoSocialButton(
            text =
                "Continuar con Apple",
            iconRes =
                R.drawable.ic_apple_auth,
            iconSize = 24.dp,
            iconTint = Color.Black,
            onClick = {
                onSocialLoginClick(
                    "Apple"
                )
            }
        )

        Spacer(
            Modifier.height(9.dp)
        )

        PortoSocialButton(
            text =
                "Continuar con Google",
            iconRes =
                R.drawable.ic_google_auth,
            iconSize = 20.dp,
            iconTint = Color.Unspecified,
            onClick = {
                onSocialLoginClick(
                    "Google"
                )
            }
        )

        Spacer(
            Modifier.height(19.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Spacer(
                Modifier.weight(1f)
            )

            Text(
                text =
                    "¿No tienes cuenta? ",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 11.5.sp,
                color =
                    PortoTextSecondary
            )

            Text(
                text =
                    "Crear cuenta",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 11.5.sp,
                color = PortoPrimary,
                modifier =
                    Modifier.clickable(
                        onClick =
                            onRegisterClick
                    )
            )

            Spacer(
                Modifier.weight(1f)
            )
        }

        Spacer(
            Modifier.height(28.dp)
        )
    }
}

@Preview(
    name = "Login - Sin flecha",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 402,
    heightDp = 874
)
// Componentes internos de la pantalla.

@Composable
private fun LoginScreenWithoutBackPreview() {
    LoginScreen(
        showBackButton = false,
        onBackClick = {},
        onLoginClick = { _, _ -> },
        onRegisterClick = {},
        onForgotPasswordClick = {},
        onSocialLoginClick = {}
    )
}