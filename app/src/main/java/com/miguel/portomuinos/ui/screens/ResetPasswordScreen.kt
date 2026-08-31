/*
 * Responsabilidad: Pantalla que establece una nueva contraseña tras recibir un deep link de
 * recuperación válido.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
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
import com.miguel.portomuinos.ui.components.PortoPrimaryButton
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun ResetPasswordScreen(
    isLoading: Boolean = false,
    authErrorText: String? = null,
    onUpdatePasswordClick:
        (String) -> Unit,
    onAuthErrorClear: () -> Unit = {}
) {

    var password by remember {
        mutableStateOf("")
    }

    var repeatedPassword by remember {
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
            Modifier.height(70.dp)
        )

        Text(
            text =
                "Nueva contraseña",
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
                "Elige una nueva contraseña para tu cuenta.",
            fontFamily = Poppins,
            fontSize = 12.5.sp,
            color = PortoTextSecondary
        )

        Spacer(
            Modifier.height(30.dp)
        )

        AuthTextField(
            value = password,
            onValueChange = {
                password = it
                errorText = null
                onAuthErrorClear()
            },
            placeholder =
                "Nueva contraseña",
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

        Spacer(
            Modifier.height(28.dp)
        )

        PortoPrimaryButton(
            text =
                if (isLoading) {
                    "Actualizando..."
                } else {
                    "Cambiar contraseña"
                },
            enabled = !isLoading,
            onClick = {

                when {

                    password.length < 6 ->
                        errorText =
                            "La contraseña debe tener al menos 6 caracteres."

                    password !=
                            repeatedPassword ->
                        errorText =
                            "Las contraseñas no coinciden."

                    else ->
                        onUpdatePasswordClick(
                            password
                        )
                }
            }
        )
    }
}