/*
 * Responsabilidad: Pantalla conversacional del demostrador AlgaeChef y representación de sus
 * recomendaciones de recetas.
 */

package com.miguel.portomuinos.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.components.AlgaeChefQuickOption
import com.miguel.portomuinos.ui.components.PortoBackButton
import com.miguel.portomuinos.ui.components.PortoBackButtonStyle
import com.miguel.portomuinos.ui.components.PortoBottomBarOverlayClearance
import com.miguel.portomuinos.ui.components.RecipeCard
import com.miguel.portomuinos.ui.model.AlgaeChefMessageRole
import com.miguel.portomuinos.ui.model.AlgaeChefMessageUi
import com.miguel.portomuinos.ui.model.AlgaeChefSuggestionUi
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoMint
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoSurface
import com.miguel.portomuinos.ui.theme.PortoTextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlgaeChefScreen(
    quickSuggestions: List<AlgaeChefSuggestionUi>,
    messages: List<AlgaeChefMessageUi>,
    isThinking: Boolean,
    recipes: List<RecipeUi>,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {},
    onNewConversation: () -> Unit = {},
    onSendMessage: (String) -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    onFavoriteRecipeClick: (String) -> Unit = {}
) {
    // Estado local del campo de texto y del scroll conversacional.

    var prompt by rememberSaveable {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    val hasConversation = messages.isNotEmpty() || isThinking
    val hasAssistantResponse = messages.any {
        it.role == AlgaeChefMessageRole.ASSISTANT
    }

    fun submitPrompt() {
        val cleanPrompt = prompt.trim()

        if (cleanPrompt.isNotEmpty() && !isThinking) {
            prompt = ""
            focusManager.clearFocus()
            onSendMessage(cleanPrompt)
        }
    }

    // Mantiene visible el último mensaje al cambiar la conversación.

    LaunchedEffect(messages.size, isThinking) {
        delay(90)
        val lastIndex = listState.layoutInfo.totalItemsCount - 1
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    // Contenido principal: historial, estado de pensamiento y compositor.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(
                bottom =
                    PortoBottomBarOverlayClearance +
                        106.dp
            )
        ) {
            item(key = "header") {
                AlgaeChefHeader(
                    showNewConversation = hasConversation,
                    onBackClick = onBackClick,
                    onNewConversation = {
                        prompt = ""
                        focusManager.clearFocus()
                        onNewConversation()
                    }
                )
            }

            item(key = "mascot") {
                AnimatedAlgaeChefMascot(
                    compact = hasConversation,
                    isThinking = isThinking,
                    showCookingPose = hasAssistantResponse && !isThinking
                )
            }

            if (!hasConversation) {
                item(key = "intro") {
                    Text(
                        text = "Cuéntame qué tienes en casa\no qué te apetece cocinar",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "O elige una opción rápida",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        quickSuggestions.forEach { suggestion ->
                            AlgaeChefQuickOption(
                                text = suggestion.label,
                                onClick = {
                                    if (!isThinking) {
                                        onSendMessage(suggestion.prompt)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                }
            }

            items(items = messages, key = { it.id }) { message ->
                when (message.role) {
                    AlgaeChefMessageRole.USER -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .background(
                                        color = PortoDarkGreen,
                                        shape = RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = 20.dp,
                                            bottomEnd = 5.dp
                                        )
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = message.text,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    AlgaeChefMessageRole.ASSISTANT -> {
                        val result = message.result

                        if (result != null) {
                            val recipe = recipes.firstOrNull {
                                it.id == result.recipeId
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 12.dp)
                            ) {
                                AssistantMessageHeader()

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = PortoMint.copy(alpha = 0.60f),
                                            shape = RoundedCornerShape(22.dp)
                                        )
                                        .padding(18.dp)
                                ) {
                                    Text(
                                        text = "Te recomiendo usar",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = PortoPrimary
                                    )

                                    Text(
                                        text = result.recommendedSeaweed,
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 28.sp,
                                        color = PortoDarkGreen
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = Color.White,
                                                shape = RoundedCornerShape(17.dp)
                                            )
                                            .padding(15.dp)
                                    ) {
                                        Text(
                                            text = "¿Por qué?",
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = result.reason,
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 13.5.sp,
                                            lineHeight = 19.sp,
                                            color = PortoDarkGreen
                                        )
                                    }
                                }

                                if (recipe != null) {
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Receta recomendada",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(9.dp))

                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = {
                                            onRecipeClick(
                                                recipe.id
                                            )
                                        },
                                        onFavoriteClick = {
                                            onFavoriteRecipeClick(
                                                recipe.id
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isThinking) {
                item(key = "thinking") {
                    ThinkingMessage()
                }
            }
        }

        AlgaeChefComposer(
            value = prompt,
            onValueChange = { prompt = it },
            enabled = !isThinking,
            onSend = { submitPrompt() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    bottom =
                        PortoBottomBarOverlayClearance +
                            22.dp
                )
        )
    }
}

// Componentes internos de la pantalla.

@Composable
private fun AlgaeChefHeader(
    showNewConversation: Boolean,
    onBackClick: () -> Unit,
    onNewConversation: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
    ) {
        PortoBackButton(
            onClick = onBackClick,
            style = PortoBackButtonStyle.PLAIN,
            size = 44.dp,
            iconSize = 23.dp,
            modifier = Modifier.align(
                Alignment.CenterStart
            )
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AlgaeChef",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 29.sp,
                color = Color.Black
            )

            Text(
                text = "Tu asistente personal de recetas",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoPrimary
            )
        }

        if (showNewConversation) {
            Text(
                text = "Nueva",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = PortoPrimary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(onClick = onNewConversation)
                    .padding(horizontal = 6.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun AnimatedAlgaeChefMascot(
    compact: Boolean,
    isThinking: Boolean,
    showCookingPose: Boolean
) {
    val containerHeight by animateDpAsState(
        targetValue = if (compact) 172.dp else 272.dp,
        animationSpec = tween(durationMillis = 520),
        label = "mascotContainerHeight"
    )

    val blobWidth by animateDpAsState(
        targetValue = if (compact) 220.dp else 286.dp,
        animationSpec = tween(durationMillis = 520),
        label = "mascotBlobWidth"
    )

    val blobHeight by animateDpAsState(
        targetValue = if (compact) 126.dp else 205.dp,
        animationSpec = tween(durationMillis = 520),
        label = "mascotBlobHeight"
    )

    val mascotWidth by animateDpAsState(
        targetValue = if (compact) 145.dp else 215.dp,
        animationSpec = tween(durationMillis = 520),
        label = "mascotWidth"
    )

    val mascotHeight by animateDpAsState(
        targetValue = if (compact) 158.dp else 232.dp,
        animationSpec = tween(durationMillis = 520),
        label = "mascotHeight"
    )

    val cookingAlpha by animateFloatAsState(
        targetValue = if (showCookingPose) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "cookingPoseAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(
        label = "thinkingMascotTransition"
    )

    val thinkingScale by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinkingMascotScale"
    )

    val thinkingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinkingMascotOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = blobWidth, height = blobHeight)
                .background(
                    color = PortoMint.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(
                        if (compact) 38.dp else 48.dp
                    )
                )
        )

        Image(
            painter = painterResource(
                R.drawable.mascota_pensando
            ),
            contentDescription = "AlgaeChef pensando",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(
                    width = mascotWidth,
                    height = mascotHeight
                )
                .graphicsLayer {
                    alpha = 1f - cookingAlpha

                    if (isThinking) {
                        scaleX = thinkingScale
                        scaleY = thinkingScale
                        translationY = thinkingOffset
                    }
                }
        )

        Image(
            painter = painterResource(
                R.drawable.mascota_cocinando
            ),
            contentDescription = "AlgaeChef cocinando",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(
                    width = mascotWidth,
                    height = mascotHeight
                )
                .graphicsLayer {
                    alpha = cookingAlpha
                    val entranceScale = 0.96f + (0.04f * cookingAlpha)
                    scaleX = entranceScale
                    scaleY = entranceScale
                }
        )
    }
}

@Composable
private fun AssistantMessageHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlgaeChefMessageAvatar(
            thinking = false
        )

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Text(
            text = "AlgaeChef",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = PortoDarkGreen
        )
    }
}

@Composable
private fun AlgaeChefMessageAvatar(
    thinking: Boolean
) {
    val infiniteTransition =
        rememberInfiniteTransition(
            label = "algaeChefMessageAvatar"
        )

    val mascotScale by
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 760
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "messageAvatarScale"
        )

    val mascotOffset by
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = -3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 880
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "messageAvatarOffset"
        )

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = PortoMint.copy(
                    alpha = 0.30f
                ),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = PortoMint.copy(
                    alpha = 0.72f
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                if (thinking) {
                    R.drawable.mascota_pensando
                } else {
                    R.drawable.mascota_cocinando
                }
            ),
            contentDescription =
                if (thinking) {
                    "AlgaeChef pensando"
                } else {
                    "AlgaeChef"
                },
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(45.dp)
                .offset(
                    y =
                        if (thinking) {
                            1.dp
                        } else {
                            0.dp
                        }
                )
                .graphicsLayer {
                    if (thinking) {
                        scaleX = mascotScale
                        scaleY = mascotScale
                        translationY = mascotOffset
                    }
                }
        )
    }
}

@Composable
private fun ThinkingMessage() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 6.dp,
                bottom = 12.dp,
                start = 4.dp
            ),
        horizontalArrangement =
            Arrangement.Start,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        AlgaeChefMessageAvatar(
            thinking = true
        )

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Row(
            modifier = Modifier
                .background(
                    color = PortoSurface,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = PortoPrimary
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "AlgaeChef está pensando…",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = PortoDarkGreen
            )
        }
    }
}

@Composable
private fun AlgaeChefComposer(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canSend = value.isNotBlank() && enabled
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(29.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(29.dp)
            )
            .padding(start = 18.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PortoDarkGreen
            ),
            cursorBrush = SolidColor(PortoDarkGreen),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (canSend) {
                        focusManager.clearFocus()
                        onSend()
                    }
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = if (enabled) {
                                "Pregunta a AlgaeChef..."
                            } else {
                                "AlgaeChef está pensando..."
                            },
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = PortoTextSecondary
                        )
                    }

                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(
                    elevation =
                        if (canSend) {
                            2.dp
                        } else {
                            0.dp
                        },
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color =
                        if (canSend) {
                            PortoPrimary
                        } else {
                            PortoSurface
                        },
                    shape = CircleShape
                )
                .clickable(
                    enabled = canSend,
                    onClick = {
                        focusManager.clearFocus()
                        onSend()
                    }
                ),
            contentAlignment =
                Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_send_arrow_up
                ),
                contentDescription =
                    "Enviar",
                tint =
                    if (canSend) {
                        Color.White
                    } else {
                        PortoTextSecondary.copy(
                            alpha = 0.38f
                        )
                    },
                modifier =
                    Modifier.size(23.dp)
            )
        }
    }
}
