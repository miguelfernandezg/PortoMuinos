/*
 * Responsabilidad: Presenta el propósito principal de la aplicación antes del primer acceso del
 * usuario.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.AuthBackground
import com.miguel.portomuinos.ui.components.OnboardingDots
import com.miguel.portomuinos.ui.components.PortoMuinosLogo
import com.miguel.portomuinos.ui.components.PortoOutlinedButton
import com.miguel.portomuinos.ui.components.PortoPrimaryButton
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoTextSecondary
import kotlinx.coroutines.launch

private data class OnboardingPageUi(
    val mascotRes: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val pages =
        listOf(
            OnboardingPageUi(
                mascotRes =
                    R.drawable.mascota_saludando,
                title =
                    "Descubre las\nverduras del mar",
                description =
                    "Recetas, inspiración y productos de\nGalicia para cocinar con confianza."
            ),
            OnboardingPageUi(
                mascotRes =
                    R.drawable.mascota_cocinando,
                title =
                    "Cocina fácil\ncon algas",
                description =
                    "Ideas rápidas, saludables y con todo\nel sabor del mar."
            )
        )

    val pagerState =
        rememberPagerState(
            pageCount = {
                pages.size
            }
        )

    val scope =
        rememberCoroutineScope()

    AuthBackground {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            val data = pages[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 8.dp,
                        bottom = 12.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Spacer(
                    Modifier.height(18.dp)
                )

                PortoMuinosLogo(
                    modifier = Modifier.size(
                        width = 232.dp,
                        height = 113.dp
                    )
                )

                /*
                 * El escenario central usa el espacio disponible
                 * en lugar de una altura fija. Así la mascota queda
                 * visualmente centrada en distintos dispositivos.
                 */
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment =
                        Alignment.Center
                ) {
                    Image(
                        painter =
                            painterResource(
                                data.mascotRes
                            ),
                        contentDescription = null,
                        contentScale =
                            ContentScale.Fit,
                        modifier =
                            if (page == 0) {
                                Modifier
                                    .requiredSize(
                                        width =
                                            205.dp,
                                        height =
                                            260.dp
                                    )
                            } else {
                                Modifier
                                    .requiredSize(
                                        width =
                                            218.dp,
                                        height =
                                            260.dp
                                    )
                            }
                    )
                }

                Text(
                    text = data.title,
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 28.sp,
                    lineHeight = 29.sp,
                    color = PortoPrimary,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    Modifier.height(9.dp)
                )

                Text(
                    text =
                        data.description,
                    fontFamily = Poppins,
                    fontWeight =
                        FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color =
                        PortoTextSecondary,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    Modifier.height(16.dp)
                )

                OnboardingDots(
                    currentPage =
                        pagerState.currentPage,
                    pageCount =
                        pages.size
                )

                Spacer(
                    Modifier.height(22.dp)
                )

                /*
                 * Reservamos la misma altura inferior en las dos
                 * páginas para que título y mascota no "salten".
                 */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp),
                    contentAlignment =
                        Alignment.BottomCenter
                ) {
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        if (page == 0) {
                            PortoPrimaryButton(
                                text = "Siguiente",
                                onClick = {
                                    scope.launch {
                                        pagerState
                                            .animateScrollToPage(
                                                1
                                            )
                                    }
                                }
                            )
                        } else {
                            PortoPrimaryButton(
                                text =
                                    "Iniciar sesión",
                                onClick =
                                    onLoginClick
                            )

                            Spacer(
                                Modifier.height(10.dp)
                            )

                            PortoOutlinedButton(
                                text =
                                    "Crear cuenta",
                                onClick =
                                    onRegisterClick
                            )
                        }
                    }
                }
            }
        }
    }
}
