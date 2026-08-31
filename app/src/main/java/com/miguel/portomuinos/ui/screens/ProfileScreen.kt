/*
 * Responsabilidad: Pantalla de perfil con datos del usuario, actividad, opciones previstas y
 * acciones de cuenta.
 */

package com.miguel.portomuinos.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.ProfileAvatar
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

private val DeleteAccountColor =
    Color(0xFFB3261E)

private val ProfileDividerColor =
    Color(0xFFE2EBE8)

@Composable
fun ProfileScreen(
    userName: String,
    profileImageUri: String? = null,
    favoriteRecipesCount: Int = 0,
    favoriteProductsCount: Int = 0,
    contentPadding: PaddingValues =
        PaddingValues(),
    onBackClick: () -> Unit,
    onFavoritesClick: () -> Unit = {},
    onProfileImageSelected:
        (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                /*
                 * Photo Picker no requiere permiso general
                 * de almacenamiento. Intentamos conservar
                 * acceso persistente a la URI cuando el
                 * proveedor lo permite.
                 */
                try {
                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: Exception) {
                    // Algunos proveedores no exponen
                    // permiso persistible. La URI sigue
                    // siendo válida para la sesión.
                }

                onProfileImageSelected(
                    uri.toString()
                )
            }
        }

    val favoriteCount =
        favoriteRecipesCount +
            favoriteProductsCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        ProfileHeader(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            ProfileHero(
                userName = userName,
                profileImageUri =
                    profileImageUri,
                favoriteCount =
                    favoriteCount,
                onAvatarClick = {
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts
                                .PickVisualMedia
                                .ImageOnly
                        )
                    )
                }
            )

            Spacer(Modifier.height(24.dp))
            ProfileSectionTitle("Preferencias")

            ProfileGroup {
                ProfileOption(
                    title =
                        "Preferencias alimentarias",
                    subtitle =
                        "Adapta recomendaciones y recetas"
                )

                ProfileDivider()

                ProfileOption(
                    title =
                        "Alergias e intolerancias",
                    subtitle =
                        "Personaliza las propuestas de AlgaeChef"
                )
            }

            Spacer(Modifier.height(20.dp))
            ProfileSectionTitle("Tu actividad")

            ProfileGroup {
                ProfileOption(
                    title = "Mis favoritos",
                    subtitle = favoriteSubtitle(
                        recipeCount =
                            favoriteRecipesCount,
                        productCount =
                            favoriteProductsCount
                    ),
                    onClick =
                        onFavoritesClick
                )
            }

            Spacer(Modifier.height(20.dp))
            ProfileSectionTitle("Aplicación")

            ProfileGroup {
                ProfileOption(
                    title =
                        "Sobre Porto-Muiños",
                    subtitle =
                        "Conoce la marca y las verduras del mar"
                )

                ProfileDivider()

                ProfileOption(
                    title = "Ajustes",
                    subtitle =
                        "Preferencias de la aplicación"
                )
            }

            Spacer(Modifier.height(22.dp))
            ProfileSectionTitle("Cuenta")

            AccountAction(
                text = "Cerrar sesión",
                textColor = PortoDarkGreen,
                backgroundColor = PortoSurface,
                onClick = {
                    showLogoutDialog = true
                }
            )

            Spacer(Modifier.height(10.dp))

            AccountAction(
                text = "Eliminar cuenta",
                textColor =
                    DeleteAccountColor,
                backgroundColor =
                    DeleteAccountColor.copy(
                        alpha = 0.07f
                    ),
                onClick = {
                    showDeleteDialog = true
                }
            )

            Spacer(
                Modifier.height(
                    28.dp +
                        PortoBottomBarOverlayClearance
                )
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                DialogTitle("Cerrar sesión")
            },
            text = {
                DialogBody(
                    "¿Quieres cerrar tu sesión en Porto-Muiños?"
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = Poppins,
                        color =
                            PortoTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.SemiBold,
                        color = PortoPrimary
                    )
                }
            },
            containerColor = Color.White
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                DialogTitle("Eliminar cuenta")
            },
            text = {
                DialogBody(
                    "Esta acción eliminará tu cuenta y los datos asociados. No se puede deshacer."
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontFamily = Poppins,
                        color =
                            PortoTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccountClick()
                    }
                ) {
                    Text(
                        text =
                            "Eliminar cuenta",
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.SemiBold,
                        color =
                            DeleteAccountColor
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

// Componentes internos de la pantalla.

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickable(
                    onClick = onBackClick
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Icon(
                imageVector =
                    Icons.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = PortoDarkGreen,
                modifier =
                    Modifier.size(23.dp)
            )
        }

        Text(
            text = "Perfil",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 25.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun ProfileHero(
    userName: String,
    profileImageUri: String?,
    favoriteCount: Int,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush =
                    Brush.horizontalGradient(
                        listOf(
                            PortoMint.copy(
                                alpha = 0.46f
                            ),
                            PortoSurface.copy(
                                alpha = 0.72f
                            )
                        )
                    ),
                shape =
                    RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clickable(
                    onClick =
                        onAvatarClick
                ),
            contentAlignment =
                Alignment.Center
        ) {
            ProfileAvatar(
                imageUri = profileImageUri,
                size = 76.dp,
                placeholderIconSize =
                    54.dp,
                backgroundColor =
                    Color.White.copy(
                        alpha = 0.90f
                    )
            )

            Box(
                modifier = Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .offset(
                        x = (-1).dp,
                        y = (-1).dp
                    )
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(
                        PortoPrimary
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable
                            .ic_edit_profile
                    ),
                    contentDescription =
                        "Cambiar foto",
                    tint = Color.White,
                    modifier =
                        Modifier.size(13.dp)
                )
            }
        }

        Spacer(Modifier.size(15.dp))

        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text = userName,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 21.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Tu espacio personal",
                fontFamily = Poppins,
                fontSize = 12.5.sp,
                color =
                    PortoTextSecondary
            )

            if (favoriteCount > 0) {
                Spacer(
                    Modifier.height(7.dp)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color =
                                Color.White.copy(
                                    alpha = 0.78f
                                ),
                            shape =
                                RoundedCornerShape(
                                    50
                                )
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text =
                            "$favoriteCount " +
                                if (
                                    favoriteCount ==
                                        1
                                ) {
                                    "favorito"
                                } else {
                                    "favoritos"
                                },
                        fontFamily = Poppins,
                        fontWeight =
                            FontWeight.Medium,
                        fontSize = 10.5.sp,
                        color =
                            PortoDarkGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 3.dp,
                bottom = 9.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = 4.dp,
                    height = 17.dp
                )
                .background(
                    color =
                        PortoPrimary.copy(
                            alpha = 0.72f
                        ),
                    shape =
                        RoundedCornerShape(
                            50
                        )
                )
        )

        Spacer(Modifier.size(8.dp))

        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 14.sp,
            color = PortoDarkGreen
        )
    }
}

@Composable
private fun ProfileGroup(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    PortoSurface.copy(
                        alpha = 0.72f
                    ),
                shape =
                    RoundedCornerShape(19.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        content()
    }
}

@Composable
private fun ProfileOption(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(vertical = 14.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontFamily = Poppins,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color =
                    PortoTextSecondary
            )
        }

        Spacer(Modifier.size(12.dp))

        Text(
            text = "›",
            fontFamily = Poppins,
            fontWeight =
                FontWeight.Medium,
            fontSize = 25.sp,
            color =
                PortoDarkGreen.copy(
                    alpha = 0.52f
                )
        )
    }
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                ProfileDividerColor
            )
    )
}

@Composable
private fun AccountAction(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(
                color = backgroundColor,
                shape =
                    RoundedCornerShape(17.dp)
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

private fun favoriteSubtitle(
    recipeCount: Int,
    productCount: Int
): String {
    if (
        recipeCount == 0 &&
        productCount == 0
    ) {
        return "Recetas y productos que has guardado"
    }

    val parts = buildList {
        if (recipeCount > 0) {
            add(
                "$recipeCount " +
                    if (recipeCount == 1) {
                        "receta"
                    } else {
                        "recetas"
                    }
            )
        }

        if (productCount > 0) {
            add(
                "$productCount " +
                    if (productCount == 1) {
                        "producto"
                    } else {
                        "productos"
                    }
            )
        }
    }

    return parts.joinToString(" · ")
}

@Composable
private fun DialogTitle(
    text: String
) {
    Text(
        text = text,
        fontFamily = Poppins,
        fontWeight =
            FontWeight.SemiBold,
        color = Color.Black
    )
}

@Composable
private fun DialogBody(
    text: String
) {
    Text(
        text = text,
        fontFamily = Poppins,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        color = PortoTextSecondary
    )
}
