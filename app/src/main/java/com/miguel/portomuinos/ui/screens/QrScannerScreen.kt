/*
 * Responsabilidad: Pantalla de escaneo QR basada en CameraX y ML Kit para abrir productos internos
 * del catálogo.
 */

package com.miguel.portomuinos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.model.ProductUi
import com.miguel.portomuinos.ui.qr.QrProductResolver
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.delay

@Composable
fun QrScannerScreen(
    products: List<ProductUi>,
    onBackClick: () -> Unit,
    onProductDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) {
        ContextCompat.getMainExecutor(context)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasCameraPermission = granted
        }

    /*
     * La primera vez que se abre el scanner pedimos permiso.
     */
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    if (!hasCameraPermission) {
        CameraPermissionContent(
            onBackClick = onBackClick,
            onRequestPermission = {
                permissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        )
        return
    }

    val cameraController = remember {
        LifecycleCameraController(context)
    }

    val barcodeScanner = remember {
        val options =
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE
                )
                .build()

        BarcodeScanning.getClient(options)
    }

    /*
     * Evita procesar el mismo código varias veces mientras
     * ML Kit analiza muchos frames por segundo.
     */
    val detectionLocked =
        remember {
            AtomicBoolean(false)
        }

    var detectedProduct by remember {
        mutableStateOf<ProductUi?>(null)
    }

    var invalidQrVisible by remember {
        mutableStateOf(false)
    }

    /*
     * CameraX entrega los frames y MlKitAnalyzer los deriva
     * al BarcodeScanner de ML Kit para detectar códigos QR.
     */
    DisposableEffect(
        cameraController,
        lifecycleOwner,
        barcodeScanner
    ) {
        val analyzer =
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                mainExecutor
            ) { result ->

                if (detectionLocked.get()) {
                    return@MlKitAnalyzer
                }

                val barcodes =
                    result?.getValue(
                        barcodeScanner
                    ).orEmpty()

                val rawValue =
                    barcodes
                        .firstOrNull()
                        ?.rawValue
                        ?.trim()
                        .orEmpty()

                if (rawValue.isEmpty()) {
                    return@MlKitAnalyzer
                }

                if (
                    !detectionLocked
                        .compareAndSet(
                            false,
                            true
                        )
                ) {
                    return@MlKitAnalyzer
                }

                val product =
                    QrProductResolver.resolve(
                        rawValue = rawValue,
                        products = products
                    )

                if (product != null) {
                    detectedProduct =
                        product
                } else {
                    invalidQrVisible =
                        true
                }
            }

        cameraController
            .setImageAnalysisAnalyzer(
                mainExecutor,
                analyzer
            )

        cameraController
            .bindToLifecycle(
                lifecycleOwner
            )

        onDispose {
            cameraController
                .clearImageAnalysisAnalyzer()

            cameraController.unbind()
        }
    }

    DisposableEffect(barcodeScanner) {
        onDispose {
            barcodeScanner.close()
        }
    }

    // Un producto válido muestra feedback breve antes de navegar.
    LaunchedEffect(
        detectedProduct?.id
    ) {
        val product =
            detectedProduct
                ?: return@LaunchedEffect

        delay(1800)

        onProductDetected(
            product.id
        )
    }

    // Si el código no se reconoce, se informa y se reactiva el análisis.
    LaunchedEffect(
        invalidQrVisible
    ) {
        if (!invalidQrVisible) {
            return@LaunchedEffect
        }

        delay(2000)

        invalidQrVisible = false
        detectionLocked.set(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        AndroidView(
            factory = { previewContext ->
                PreviewView(
                    previewContext
                ).apply {
                    scaleType =
                        PreviewView
                            .ScaleType
                            .FILL_CENTER

                    implementationMode =
                        PreviewView
                            .ImplementationMode
                            .COMPATIBLE

                    controller =
                        cameraController
                }
            },
            modifier =
                Modifier.fillMaxSize()
        )

        /*
         * Capa oscura suave.
         * El marco central conserva suficiente visibilidad
         * para apuntar correctamente al QR.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.24f
                    )
                )
        )

        ScannerFrame(
            modifier =
                Modifier.align(
                    Alignment.Center
                )
                    .offset(
                        y = (-34).dp
                    )
        )

        ScannerTopBar(
            onBackClick =
                onBackClick,
            modifier =
                Modifier.align(
                    Alignment.TopCenter
                )
        )

        AnimatedVisibility(
            visible =
                detectedProduct == null &&
                    !invalidQrVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        ) {
            ScannerHelpCard()
        }

        AnimatedVisibility(
            visible =
                detectedProduct != null,
            enter =
                fadeIn() +
                    scaleIn(
                        initialScale = 0.94f
                    ),
            exit =
                fadeOut() +
                    scaleOut(),
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        ) {
            detectedProduct?.let {
                    product ->

                ProductDetectedOverlay(
                    productName =
                        product.name
                )
            }
        }

        AnimatedVisibility(
            visible =
                invalidQrVisible,
            enter =
                fadeIn() +
                    scaleIn(
                        initialScale = 0.96f
                    ),
            exit =
                fadeOut() +
                    scaleOut(),
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        ) {
            InvalidQrOverlay()
        }
    }
}

// Componentes internos de la pantalla.

@Composable
private fun ScannerTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
            .padding(
                horizontal = 18.dp
            )
    ) {

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.OVERLAY,
            size = 42.dp,
            iconSize = 22.dp,
            modifier = Modifier.align(
                Alignment.CenterStart
            )
        )

        Text(
            text =
                "Escanear producto",
            fontFamily =
                Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 20.sp,
            color =
                Color.White,
            modifier =
                Modifier.align(
                    Alignment.Center
                )
        )
    }
}

@Composable
private fun ScannerFrame(
    modifier: Modifier = Modifier
) {
    val frameSize = 252.dp
    val cornerLength = 38.dp

    val transition =
        rememberInfiniteTransition(
            label =
                "qrLaserTransition"
        )

    val laserOffset by
        transition.animateFloat(
            initialValue = -82f,
            targetValue = 82f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1800
                        ),
                    repeatMode =
                        RepeatMode.Reverse
                ),
            label =
                "qrLaserOffset"
        )

    Box(
        modifier = modifier
            .size(frameSize)
    ) {

        Canvas(
            modifier =
                Modifier.fillMaxSize()
        ) {
            val stroke =
                3.dp.toPx()

            val corner =
                cornerLength.toPx()

            val width =
                size.width

            val height =
                size.height

            val color =
                Color.White

            // Superior izquierda
            drawLine(
                color = color,
                start = Offset(0f, corner),
                end = Offset(0f, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(corner, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // Superior derecha
            drawLine(
                color = color,
                start = Offset(
                    width - corner,
                    0f
                ),
                end = Offset(
                    width,
                    0f
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(
                    width,
                    0f
                ),
                end = Offset(
                    width,
                    corner
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // Inferior izquierda
            drawLine(
                color = color,
                start = Offset(
                    0f,
                    height - corner
                ),
                end = Offset(
                    0f,
                    height
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(
                    0f,
                    height
                ),
                end = Offset(
                    corner,
                    height
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // Inferior derecha
            drawLine(
                color = color,
                start = Offset(
                    width - corner,
                    height
                ),
                end = Offset(
                    width,
                    height
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(
                    width,
                    height
                ),
                end = Offset(
                    width,
                    height - corner
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }

        Box(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .offset(
                    y = laserOffset.dp
                )
                .width(184.dp)
                .height(2.dp)
                .background(
                    color =
                        PortoMint.copy(
                            alpha = 0.92f
                        ),
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
        )
    }
}

@Composable
private fun ScannerHelpCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(
                start = 22.dp,
                end = 22.dp,
                bottom = 28.dp
            )
            .fillMaxWidth()
            .background(
                color =
                    Color.White.copy(
                        alpha = 0.94f
                    ),
                shape =
                    RoundedCornerShape(
                        24.dp
                    )
            )
            .padding(
                horizontal = 22.dp,
                vertical = 18.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "Escanea el QR del producto",
            fontFamily =
                Poppins,
            fontWeight =
                FontWeight.SemiBold,
            fontSize = 17.sp,
            color =
                PortoDarkGreen,
            textAlign =
                TextAlign.Center
        )

        Spacer(
            Modifier.height(5.dp)
        )

        Text(
            text =
                "Coloca el código dentro del recuadro. Abriremos automáticamente su ficha.",
            fontFamily =
                Poppins,
            fontWeight =
                FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color =
                PortoDarkGreen.copy(
                    alpha = 0.72f
                ),
            textAlign =
                TextAlign.Center
        )
    }
}

@Composable
private fun ProductDetectedOverlay(
    productName: String
) {
    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                horizontal = 22.dp,
                vertical = 28.dp
            )
            .fillMaxWidth()
            .background(
                color =
                    PortoMint.copy(
                        alpha = 0.97f
                    ),
                shape =
                    RoundedCornerShape(
                        22.dp
                    )
            )
            .border(
                width = 1.dp,
                color =
                    PortoPrimary.copy(
                        alpha = 0.20f
                    ),
                shape =
                    RoundedCornerShape(
                        22.dp
                    )
            )
            .padding(
                horizontal = 18.dp,
                vertical = 15.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color =
                        PortoPrimary,
                    shape =
                        CircleShape
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Icon(
                imageVector =
                    Icons.Outlined.Check,
                contentDescription =
                    null,
                tint =
                    Color.White,
                modifier =
                    Modifier.size(24.dp)
            )
        }

        Spacer(
            Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    "Producto detectado",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 13.sp,
                color =
                    PortoPrimary
            )

            Text(
                text =
                    productName,
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                color =
                    PortoDarkGreen
            )

            Spacer(
                Modifier.height(2.dp)
            )

            Text(
                text =
                    "Abriendo ficha…",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 12.sp,
                color =
                    PortoDarkGreen.copy(
                        alpha = 0.68f
                    )
            )
        }
    }
}

@Composable
private fun InvalidQrOverlay() {
    val errorColor =
        Color(0xFFD95858)

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                horizontal = 22.dp,
                vertical = 28.dp
            )
            .fillMaxWidth()
            .background(
                color =
                    Color.White.copy(
                        alpha = 0.97f
                    ),
                shape =
                    RoundedCornerShape(
                        22.dp
                    )
            )
            .border(
                width = 1.dp,
                color =
                    errorColor.copy(
                        alpha = 0.22f
                    ),
                shape =
                    RoundedCornerShape(
                        22.dp
                    )
            )
            .padding(
                horizontal = 18.dp,
                vertical = 15.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .background(
                    color =
                        errorColor,
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
        )

        Spacer(
            Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    "Código no reconocido",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 15.sp,
                color =
                    errorColor
            )

            Spacer(
                Modifier.height(3.dp)
            )

            Text(
                text =
                    "Este QR no corresponde a un producto disponible en la app.",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color =
                    PortoDarkGreen.copy(
                        alpha = 0.70f
                    )
            )
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onBackClick: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN,
            size = 44.dp,
            iconSize = 23.dp,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = 18.dp,
                    top = 14.dp
                )
        )

        Column(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(
                    horizontal = 34.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        color =
                            PortoMint.copy(
                                alpha = 0.42f
                            ),
                        shape =
                            CircleShape
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
                        null,
                    tint =
                        PortoPrimary,
                    modifier =
                        Modifier.size(36.dp)
                )
            }

            Spacer(
                Modifier.height(22.dp)
            )

            Text(
                text =
                    "Necesitamos acceso a la cámara",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 21.sp,
                color =
                    Color.Black,
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text =
                    "La cámara se utiliza únicamente para leer el código QR del producto.",
                fontFamily =
                    Poppins,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color =
                    PortoDarkGreen.copy(
                        alpha = 0.70f
                    ),
                textAlign =
                    TextAlign.Center
            )

            Spacer(
                Modifier.height(24.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color =
                            PortoDarkGreen,
                        shape =
                            RoundedCornerShape(
                                50.dp
                            )
                    )
                    .clickable(
                        onClick =
                            onRequestPermission
                    )
                    .padding(
                        horizontal = 28.dp,
                        vertical = 13.dp
                    )
            ) {
                Text(
                    text =
                        "Permitir cámara",
                    fontFamily =
                        Poppins,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color =
                        Color.White
                )
            }
        }
    }
}
