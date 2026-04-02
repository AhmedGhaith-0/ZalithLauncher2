/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.theme.feativals

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.movtery.zalithlauncher.utils.festival.Festival
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FestivalTitleText(
    festivals: List<Festival>,
    style: TextStyle,
    softWrap: Boolean,
    maxLines: Int,
    modifier: Modifier = Modifier
) {
    if (festivals.isEmpty()) return

    val infiniteTransition = rememberInfiniteTransition(label = "title_text_scale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_text_scale_anim"
    )

    var festival by remember { mutableStateOf(festivals.first()) }
    if (festivals.size > 1) {
        LaunchedEffect(Unit) {
            var index = 0
            while (isActive) {
                delay(5000)
                index = (index + 1) % festivals.size
                festival = festivals[index]
            }
        }
    }

    Text(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        text = stringResource(festival.textRes),
        style = style,
        color = Color.Yellow,
        softWrap = softWrap,
        maxLines = maxLines
    )
}