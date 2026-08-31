/*
 * Responsabilidad: Pantalla de registro de usuario mediante correo electrónico y contraseña.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.components.AuthTextField
import com.miguel.portomuinos.ui.components.PortoPrimaryButton
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun RegisterScreen(
    authErrorText: String? = null,
    isLoading: Boolean = false,
    onAuthErrorClear: () -> Unit = {},
    onBackClick: () -> Unit,
    onRegisterClick:
        (
        fullName: String,
        email: String,
        password: String
    ) -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var repeatedPassword by remember {
        mutableStateOf("")
    }

    var acceptedTerms by remember {
        mutableStateOf(false)
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

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN,
            size = 44.dp,
            iconSize = 23.dp
        )

        /*
         * Margen superior suficiente para separar el formulario
         * de los elementos del sistema y mantener la jerarquía visual.
         */
        Spacer(
            Modifier.height(45.dp)
        )

        Text(
            text = "Crear cuenta",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 21.sp,
            color = Color.Black,
            modifier =
                Modifier.padding(
                    start = 2.dp
                )
        )

        Spacer(
            Modifier.height(3.dp)
        )

        Text(
            text =
                "Únete a la comunidad de Porto-Muiños",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.Normal,
            fontSize = 12.5.sp,
            color = PortoTextSecondary,
            modifier =
                Modifier.padding(
                    start = 2.dp
                )
        )

        Spacer(
            Modifier.height(30.dp)
        )

        AuthTextField(
            value = fullName,
            onValueChange = {
                /*
                 * No se filtra ningún carácter Unicode:
                 * á, é, í, ó, ú, ñ, etc. son válidos.
                 */
                fullName = it
                errorText = null
                onAuthErrorClear()
            },
            placeholder =
                "Nombre completo",
            leadingIconRes =
                R.drawable.ic_auth_person,
            keyboardType =
                KeyboardType.Text,
            capitalization =
                KeyboardCapitalization.Words
        )

        Spacer(
            Modifier.height(13.dp)
        )

        AuthTextField(
            value = email,
            onValueChange = {
                email = it
                errorText = null
                onAuthErrorClear()
            },
            placeholder =
                "Correo electrónico",
            leadingIconRes =
                R.drawable.ic_auth_email,
            keyboardType =
                KeyboardType.Email
        )

        Spacer(
            Modifier.height(13.dp)
        )

        AuthTextField(
            value = password,
            onValueChange = {
                password = it
                errorText = null
                onAuthErrorClear()
            },
            placeholder =
                "Contraseña",
            leadingIconRes =
                R.drawable.ic_auth_lock,
            isPassword = true,
            keyboardType =
                KeyboardType.Password
        )

        Spacer(
            Modifier.height(13.dp)
        )

        AuthTextField(
            value = repeatedPassword,
            onValueChange = {
                repeatedPassword = it
                errorText = null
                onAuthErrorClear()
            },
            placeholder =
                "Repetir contraseña",
            leadingIconRes =
                R.drawable.ic_auth_lock,
            isPassword = true,
            keyboardType =
                KeyboardType.Password
        )

        Spacer(
            Modifier.height(19.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 6.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * Checkbox propio: borde limpio al estar vacío y
             * exactamente el mismo verde al estar seleccionado.
             */
            Box(
                modifier = Modifier
                    .size(23.dp)
                    .background(
                        color =
                            if (acceptedTerms) {
                                PortoPrimary
                            } else {
                                Color.White
                            },
                        shape =
                            RoundedCornerShape(
                                3.dp
                            )
                    )
                    .border(
                        width =
                            if (acceptedTerms) {
                                0.dp
                            } else {
                                1.4.dp
                            },
                        color =
                            if (acceptedTerms) {
                                Color.Transparent
                            } else {
                                Color(0xFF8D9492)
                            },
                        shape =
                            RoundedCornerShape(
                                3.dp
                            )
                    )
                    .clickable {
                        acceptedTerms =
                            !acceptedTerms
                        errorText = null
                        onAuthErrorClear()
                    },
                contentAlignment =
                    Alignment.Center
            ) {
                if (acceptedTerms) {
                    Canvas(
                        modifier =
                            Modifier.size(13.dp)
                    ) {
                        val stroke =
                            2.dp.toPx()

                        /*
                         * Check minimalista construido con dos
                         * segmentos rectos. No depende de ningún
                         * carácter tipográfico.
                         */
                        drawLine(
                            color = Color.White,
                            start = Offset(
                                x = size.width * 0.12f,
                                y = size.height * 0.53f
                            ),
                            end = Offset(
                                x = size.width * 0.39f,
                                y = size.height * 0.78f
                            ),
                            strokeWidth = stroke,
                            cap = StrokeCap.Square
                        )

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                x = size.width * 0.39f,
                                y = size.height * 0.78f
                            ),
                            end = Offset(
                                x = size.width * 0.88f,
                                y = size.height * 0.20f
                            ),
                            strokeWidth = stroke,
                            cap = StrokeCap.Square
                        )
                    }
                }
            }

            Spacer(
                Modifier.size(11.dp)
            )

            Text(
                text =
                    buildAnnotatedString {
                        append("Acepto los ")

                        withStyle(
                            SpanStyle(
                                color =
                                    PortoPrimary,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        ) {
                            append(
                                "términos y condiciones"
                            )
                        }

                        append("\ny la ")

                        withStyle(
                            SpanStyle(
                                color =
                                    PortoPrimary,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        ) {
                            append(
                                "política de privacidad"
                            )
                        }
                    },
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = PortoDarkGreen
            )
        }

        val visibleErrorText =
            errorText ?: authErrorText

        if (visibleErrorText != null) {
            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    visibleErrorText,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 11.5.sp,
                color =
                    Color(0xFFC94B4B)
            )
        }

        Spacer(
            Modifier.height(30.dp)
        )

        PortoPrimaryButton(
            text =
                if (isLoading) {
                    "Creando cuenta..."
                } else {
                    "Crear cuenta"
                },
            enabled = !isLoading,
            onClick = {
                when {
                    fullName.isBlank() ->
                        errorText =
                            "Introduce tu nombre."

                    !email.contains("@") ->
                        errorText =
                            "Introduce un correo electrónico válido."

                    password.length < 6 ->
                        errorText =
                            "La contraseña debe tener al menos 6 caracteres."

                    password !=
                        repeatedPassword ->
                        errorText =
                            "Las contraseñas no coinciden."

                    !acceptedTerms ->
                        errorText =
                            "Debes aceptar los términos y la política de privacidad."

                    else ->
                        onRegisterClick(
                            fullName.trim(),
                            email.trim(),
                            password
                        )
                }
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
                    "¿Ya tienes cuenta? ",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 11.5.sp,
                color =
                    PortoTextSecondary
            )

            Text(
                text =
                    "Inicia sesión",
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 11.5.sp,
                color = PortoPrimary,
                modifier =
                    Modifier.clickable(
                        onClick =
                            onLoginClick
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
