/*
 * Responsabilidad: Implementa la navegación inferior principal y el botón central de acceso al
 * escáner QR.
 */

package com.miguel.portomuinos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoWhite

enum class BottomNavItem {
    HOME,
    RECIPES,
    ALGAECHEF,
    STORE
}

/**
 * Espacio que deben liberar las pantallas para que su contenido
 * quede visible por encima de la navegación flotante.
 *
 * La parte fija corresponde a la propia barra y su margen inferior.
 * Se suma el inset real de la barra de navegación del sistema para
 * adaptarse tanto a navegación por gestos como a navegación clásica
 * de tres botones.
 */
private val PortoBottomBarBaseClearance = 84.dp

val PortoBottomBarOverlayClearance: Dp
    @Composable
    get() =
        PortoBottomBarBaseClearance +
            WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()

private data class BottomNavVisualItem(
    val label: String,
    @DrawableRes val outlineIcon: Int,
    @DrawableRes val filledIcon: Int,
    /*
     * Los SVG originales no ocupan exactamente la misma posición
     * dentro de sus viewports. Este pequeño ajuste óptico evita que
     * Recetas, AlgaeChef y Tienda parezcan más altos que Inicio.
     */
    val iconOffsetY: Dp = 0.dp
)

private val homeItem =
    BottomNavVisualItem(
        label = "Inicio",
        outlineIcon =
            R.drawable.ic_home_outline_bold,
        filledIcon =
            R.drawable.ic_home_filled
    )

private val recipesItem =
    BottomNavVisualItem(
        label = "Recetas",
        outlineIcon =
            R.drawable.ic_recipes_outline_bold,
        filledIcon =
            R.drawable.ic_recipes_filled,
        iconOffsetY = 0.5.dp
    )

private val algaeChefItem =
    BottomNavVisualItem(
        label = "AlgaeChef",
        outlineIcon =
            R.drawable.ic_algaechef_outline_bold,
        filledIcon =
            R.drawable.ic_algaechef_filled,
        iconOffsetY = 1.5.dp
    )

private val storeItem =
    BottomNavVisualItem(
        label = "Tienda",
        outlineIcon =
            R.drawable.ic_store_outline_bold,
        filledIcon =
            R.drawable.ic_store_filled,
        iconOffsetY = 0.75.dp
    )

/*
 * La barra utiliza una muesca cóncava real para integrar el botón
 * central del escáner sin superponer un fondo circular adicional.
 */
private val FloatingNotchedShape =
    GenericShape { size, _ ->

        val width =
            size.width
        val height =
            size.height
        val centerX =
            width / 2f

        val corner =
            height / 2f

        /*
         * La muesca se construye con dos curvas Bézier simétricas
         * para conservar continuidad visual alrededor del escáner.
         */
        val notchHalfWidth =
            height * 0.72f

        val notchDepth =
            height * 0.54f

        val notchStart =
            centerX -
                notchHalfWidth

        val notchEnd =
            centerX +
                notchHalfWidth

        moveTo(
            x = corner,
            y = 0f
        )

        lineTo(
            x = notchStart,
            y = 0f
        )

        /*
         * Mitad izquierda.
         */
        cubicTo(
            x1 =
                notchStart +
                    height * 0.20f,
            y1 = 0f,
            x2 =
                centerX -
                    height * 0.47f,
            y2 =
                notchDepth,
            x3 =
                centerX,
            y3 =
                notchDepth
        )

        /*
         * Mitad derecha simétrica.
         */
        cubicTo(
            x1 =
                centerX +
                    height * 0.47f,
            y1 =
                notchDepth,
            x2 =
                notchEnd -
                    height * 0.20f,
            y2 = 0f,
            x3 =
                notchEnd,
            y3 = 0f
        )

        lineTo(
            x =
                width - corner,
            y = 0f
        )

        quadraticBezierTo(
            x1 = width,
            y1 = 0f,
            x2 = width,
            y2 = corner
        )

        lineTo(
            x = width,
            y =
                height - corner
        )

        quadraticBezierTo(
            x1 = width,
            y1 = height,
            x2 =
                width - corner,
            y2 = height
        )

        lineTo(
            x = corner,
            y = height
        )

        quadraticBezierTo(
            x1 = 0f,
            y1 = height,
            x2 = 0f,
            y2 =
                height - corner
        )

        lineTo(
            x = 0f,
            y = corner
        )

        quadraticBezierTo(
            x1 = 0f,
            y1 = 0f,
            x2 = corner,
            y2 = 0f
        )

        close()
    }

private val ActiveShape =
    RoundedCornerShape(18.dp)

@Composable
fun PortoBottomBar(
    selectedItem: BottomNavItem,
    onHomeClick: () -> Unit,
    onRecipesClick: () -> Unit,
    onQrClick: () -> Unit,
    onAlgaeChefClick: () -> Unit,
    onStoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                start = 14.dp,
                end = 14.dp,
                bottom = 4.dp
            )
            .height(80.dp),
        contentAlignment =
            Alignment.BottomCenter
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = FloatingNotchedShape,
                    clip = false
                )
                .background(
                    color =
                        PortoWhite.copy(
                            alpha = 0.99f
                        ),
                    shape =
                        FloatingNotchedShape
                )
                /*
                 * El borde sigue la propia muesca y crea la línea
                 * de integración alrededor del scanner.
                 */
                .border(
                    width = 0.75.dp,
                    color =
                        Color(0xFFE5EEEC),
                    shape =
                        FloatingNotchedShape
                )
                .padding(
                    horizontal = 5.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            BottomNavIconItem(
                visualItem = homeItem,
                selected =
                    selectedItem ==
                        BottomNavItem.HOME,
                onClick = onHomeClick,
                modifier =
                    Modifier.weight(1f)
            )

            BottomNavIconItem(
                visualItem = recipesItem,
                selected =
                    selectedItem ==
                        BottomNavItem.RECIPES,
                onClick = onRecipesClick,
                modifier =
                    Modifier.weight(1f)
            )

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            BottomNavIconItem(
                visualItem = algaeChefItem,
                selected =
                    selectedItem ==
                        BottomNavItem.ALGAECHEF,
                onClick = onAlgaeChefClick,
                modifier =
                    Modifier.weight(1f)
            )

            BottomNavIconItem(
                visualItem = storeItem,
                selected =
                    selectedItem ==
                        BottomNavItem.STORE,
                onClick = onStoreClick,
                modifier =
                    Modifier.weight(1f)
            )
        }

        /*
         * El botón se eleva por encima de la pastilla.
         * La muesca real de FloatingNotchedShape deja aire alrededor:
         * no existe círculo blanco intermedio.
         */
        Box(
            modifier = Modifier
                .align(
                    Alignment.TopCenter
                )
                .offset(
                    y = 0.dp
                )
                .size(54.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color = PortoPrimary,
                    shape = CircleShape
                )
                .clickable(
                    onClick = onQrClick
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Icon(
                painter =
                    painterResource(
                        R.drawable.ic_scanner
                    ),
                contentDescription =
                    "Escanear producto",
                tint = Color.Unspecified,
                modifier =
                    Modifier.size(28.dp)
            )
        }
    }
}

// Utilidades internas del componente.

@Composable
private fun BottomNavIconItem(
    visualItem: BottomNavVisualItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .height(54.dp)
            .clickable(
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {

        /*
         * Foco horizontal, sin borde.
         * Incluye icono + texto y deja la barra visualmente más baja.
         */
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(46.dp)
                .then(
                    if (selected) {
                        Modifier.background(
                            brush =
                                Brush.horizontalGradient(
                                    colors =
                                        listOf(
                                            PortoMint.copy(
                                                alpha = 0.82f
                                            ),
                                            PortoPrimary.copy(
                                                alpha = 0.30f
                                            ),
                                            PortoMint.copy(
                                                alpha = 0.82f
                                            )
                                        )
                                ),
                            shape =
                                ActiveShape
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Icon(
                    painter =
                        painterResource(
                            if (selected) {
                                visualItem.filledIcon
                            } else {
                                visualItem.outlineIcon
                            }
                        ),
                    contentDescription =
                        visualItem.label,
                    tint =
                        if (selected) {
                            Color.Unspecified
                        } else {
                            PortoPrimary.copy(
                                alpha = 0.58f
                            )
                        },
                    modifier =
                        Modifier
                            .size(32.dp)
                            .offset(
                                y =
                                    visualItem.iconOffsetY
                            )
                )

                Text(
                    text =
                        visualItem.label,
                    fontFamily =
                        Poppins,
                    fontWeight =
                        if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        },
                    fontSize = 9.2.sp,
                    lineHeight = 9.2.sp,
                    color =
                        if (selected) {
                            PortoDarkGreen
                        } else {
                            PortoPrimary.copy(
                                alpha = 0.58f
                            )
                        },
                    modifier =
                        Modifier.offset(
                            y = (-4).dp
                        )
                )
            }
        }
    }
}
