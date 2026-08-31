/*
 * Responsabilidad: Tarjeta reutilizable de receta con metadatos básicos y control de favorito.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.ui.model.RecipeUi
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoDarkGreen
import com.miguel.portomuinos.ui.theme.PortoSurface

private val RecipeCardShape = RoundedCornerShape(17.dp)

@Composable
fun RecipeCard(
    recipe: RecipeUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
            .shadow(3.dp, RecipeCardShape)
            .clip(RecipeCardShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        RecipeCardImage(
            recipe = recipe,
            modifier = Modifier.width(126.dp).fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 13.dp, end = 8.dp, top = 8.dp, bottom = 9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = recipe.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 17.5.sp,
                    color = PortoDarkGreen,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                /*
                 * Separación mínima garantizada.
                 * Evita que títulos largos parezcan tocar el corazón.
                 */
                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                FavoriteIconButton(
                    isFavorite = recipe.isFavorite,
                    onClick = onFavoriteClick,
                    size = 31.dp,
                    iconSize = 18.dp,
                    backgroundColor = PortoSurface
                )
            }
            Spacer(Modifier.weight(1f))
            RecipeMetaRow(
                durationMinutes = recipe.durationMinutes,
                difficulty = recipe.difficulty
            )
        }
    }
}

// Utilidades internas del componente.

@Composable
private fun RecipeCardImage(
    recipe: RecipeUi,
    modifier: Modifier = Modifier
) {
    CatalogImage(
        imageRes = recipe.imageRes,
        imageUrl = recipe.imageUrl,
        contentDescription = recipe.title,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
