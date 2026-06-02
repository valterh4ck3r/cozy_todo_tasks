package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.valternegreiros.cozy_todo_task.ui.theme.CozyGreen
import com.valternegreiros.cozy_todo_task.ui.theme.CozyGreenDark
import com.valternegreiros.cozy_todo_task.ui.theme.CozyOrangeDark

@Composable
internal fun DecorativeLeaves() {
    Canvas(Modifier.fillMaxSize().alpha(0.16f)) {
        val leaf = Path().apply {
            moveTo(0f, -18f)
            cubicTo(22f, -12f, 22f, 12f, 0f, 18f)
            cubicTo(-18f, 8f, -18f, -8f, 0f, -18f)
        }
        listOf(
            Offset(size.width * 0.08f, size.height * 0.12f),
            Offset(size.width * 0.88f, size.height * 0.18f),
            Offset(size.width * 0.16f, size.height * 0.78f),
            Offset(size.width * 0.78f, size.height * 0.86f)
        ).forEachIndexed { index, offset ->
            rotate(index * 34f, offset) {
                translate(offset.x, offset.y) {
                    drawPath(leaf, CozyGreen)
                }
            }
        }
    }
}

@Composable
internal fun PlantPot(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(CozyGreen, radius = size.minDimension * 0.13f, center = Offset(size.width * 0.35f, size.height * 0.32f))
        drawCircle(CozyGreenDark, radius = size.minDimension * 0.15f, center = Offset(size.width * 0.56f, size.height * 0.24f))
        drawCircle(CozyGreen, radius = size.minDimension * 0.12f, center = Offset(size.width * 0.66f, size.height * 0.42f))
        drawRoundRect(
            color = CozyOrangeDark,
            topLeft = Offset(size.width * 0.26f, size.height * 0.58f),
            size = Size(size.width * 0.48f, size.height * 0.28f),
            cornerRadius = CornerRadius(18f, 18f)
        )
    }
}
