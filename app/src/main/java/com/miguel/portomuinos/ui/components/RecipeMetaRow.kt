/*
 * Responsabilidad: Muestra de forma compacta la duración y dificultad de una receta.
 */

package com.miguel.portomuinos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.portomuinos.R
import com.miguel.portomuinos.ui.model.RecipeDifficulty
import com.miguel.portomuinos.ui.theme.Poppins
import com.miguel.portomuinos.ui.theme.PortoPrimary
import com.miguel.portomuinos.ui.theme.PortoTextSecondary

@Composable
fun RecipeMetaRow(
    durationMinutes: Int,
    difficulty: RecipeDifficulty,
    modifier: Modifier = Modifier,
    groupSpacing: Dp = 12.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(groupSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_time_compact),
                contentDescription = null,
                tint = PortoTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "$durationMinutes min",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = PortoTextSecondary
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            DifficultyBars(level = difficulty.level)
            Spacer(Modifier.width(5.dp))
            Text(
                text = difficulty.label,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = PortoTextSecondary
            )
        }
    }
}

// Utilidades internas del componente.

@Composable
private fun DifficultyBars(level: Int) {
    val safeLevel = level.coerceIn(1, 3)
    Row(
        modifier = Modifier
            .width(20.dp)
            .height(18.dp),
        horizontalArrangement =
            Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(
            7.dp,
            12.dp,
            16.dp
        ).forEachIndexed { index, barHeight ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(
                        RoundedCornerShape(1.5.dp)
                    )
                    .background(
                        if (index < safeLevel) {
                            PortoPrimary.copy(
                                alpha = 0.65f
                            )
                        } else {
                            PortoTextSecondary.copy(
                                alpha = 0.20f
                            )
                        }
                    )
            )
        }
    }
}
