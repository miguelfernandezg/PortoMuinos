/*
 * Responsabilidad: Pantalla para solicitar el envío de un enlace de recuperación de contraseña.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.AuthTextField
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.components.PortoPrimaryButton
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun ForgotPasswordScreen(
    isLoading: Boolean = false,
    authErrorText: String? = null,
    infoText: String? = null,
    onBackClick: () -> Unit,
    onSendClick: (String) -> Unit,
    onAuthMessageClear: () -> Unit = {}
) {

    var email by remember {
        mutableStateOf("")
    }

    var errorText by remember {
        mutableStateOf<String?>(null)
    }

    val visibleError =
        errorText ?: authErrorText

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
            style =
                PortoBackButtonStyle.PLAIN,
            size = 44.dp,
            iconSize = 23.dp
        )

        Spacer(
            Modifier.height(45.dp)
        )

        Text(
            text =
                "Recuperar contraseña",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 21.sp,
            color = Color.Black
        )

        Spacer(
            Modifier.height(5.dp)
        )

        Text(
            text =
                "Introduce el correo electrónico asociado a tu cuenta.",
            fontFamily = Poppins,
            fontSize = 12.5.sp,
            color = PortoTextSecondary
        )

        Spacer(
            Modifier.height(30.dp)
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
                KeyboardType.Email
        )

        if (visibleError != null) {

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text = visibleError,
                fontFamily = Poppins,
                fontSize = 11.5.sp,
                color =
                    Color(0xFFC94B4B)
            )
        }

        if (
            infoText != null &&
            visibleError == null
        ) {

            Spacer(
                Modifier.height(16.dp)
            )

            Text(
                text = infoText,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PortoPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            Color(0xFFEAF4F0),
                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    )
                    .padding(14.dp)
            )
        }

        Spacer(
            Modifier.height(28.dp)
        )

        PortoPrimaryButton(
            text =
                if (isLoading) {
                    "Enviando..."
                } else {
                    "Enviar enlace"
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

                    else ->
                        onSendClick(
                            email.trim()
                        )
                }
            }
        )
    }
}