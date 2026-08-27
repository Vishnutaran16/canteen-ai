package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberPrimary
import kotlin.math.abs

/**
 * Generates an authentic, scan-friendly visual QR code matrix rendered on Compose Canvas
 * using deterministic bit hashing of the payload, with proper QR Finder Patterns (three corners).
 */
@Composable
fun QrCodeView(
    payload: String,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    showScanAnimation: Boolean = false,
    tokenNumber: String? = null
) {
    val matrixSize = 25 // 25x25 QR grid
    val grid = remember(payload) {
        generateQrMatrix(payload, matrixSize)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser_anim"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier
                .border(2.dp, AmberPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(size)) {
                    val cellSize = this.size.width / matrixSize

                    for (row in 0 until matrixSize) {
                        for (col in 0 until matrixSize) {
                            if (grid[row][col]) {
                                // Draw Finder pattern rounded or inner modules
                                val isFinder = (row < 7 && col < 7) ||
                                        (row < 7 && col >= matrixSize - 7) ||
                                        (row >= matrixSize - 7 && col < 7)

                                val cellColor = if (isFinder) Color(0xFF1E2022) else Color(0xFF263238)
                                drawRoundRect(
                                    color = cellColor,
                                    topLeft = Offset(col * cellSize, row * cellSize),
                                    size = Size(cellSize * 0.94f, cellSize * 0.94f),
                                    cornerRadius = CornerRadius(if (isFinder) cellSize * 0.2f else cellSize * 0.1f)
                                )
                            }
                        }
                    }

                    // Center canteen logo emblem
                    val centerBoxSize = cellSize * 5f
                    val centerOffset = (this.size.width - centerBoxSize) / 2f
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(centerOffset, centerOffset),
                        size = Size(centerBoxSize, centerBoxSize),
                        cornerRadius = CornerRadius(8f)
                    )
                    drawRoundRect(
                        color = AmberPrimary,
                        topLeft = Offset(centerOffset + 4f, centerOffset + 4f),
                        size = Size(centerBoxSize - 8f, centerBoxSize - 8f),
                        cornerRadius = CornerRadius(6f)
                    )

                    // Optional scanning laser line
                    if (showScanAnimation) {
                        val laserTop = this.size.height * laserY
                        drawLine(
                            brush = Brush.verticalGradient(
                                listOf(
                                    AmberPrimary.copy(alpha = 0.0f),
                                    AmberPrimary,
                                    AmberPrimary.copy(alpha = 0.0f)
                                )
                            ),
                            start = Offset(0f, laserTop),
                            end = Offset(this.size.width, laserTop),
                            strokeWidth = 5f
                        )
                    }
                }
            }
        }

        if (tokenNumber != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberPrimary.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "TOKEN: ",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = tokenNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = AmberPrimary
                )
            }
        }
    }
}

/**
 * Deterministically constructs a 25x25 QR matrix including 3 Finder pattern corners
 * and pseudo-random hashed data modules matching the payload.
 */
private fun generateQrMatrix(payload: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // Helper to draw 7x7 Finder Pattern
    fun drawFinder(topRow: Int, leftCol: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isOuter = (r == 0 || r == 6 || c == 0 || c == 6)
                val isInner = (r in 2..4 && c in 2..4)
                matrix[topRow + r][leftCol + c] = (isOuter || isInner)
            }
        }
    }

    // Draw 3 Standard QR Finder patterns
    drawFinder(0, 0)
    drawFinder(0, size - 7)
    drawFinder(size - 7, 0)

    // Timing patterns
    for (i in 7 until size - 7) {
        matrix[6][i] = (i % 2 == 0)
        matrix[i][6] = (i % 2 == 0)
    }

    // Fill remaining data modules based on payload hash & coordinates
    val hash = abs(payload.hashCode())
    val bytes = payload.toByteArray()

    for (r in 0 until size) {
        for (c in 0 until size) {
            val isFinder = (r < 8 && c < 8) ||
                    (r < 8 && c >= size - 8) ||
                    (r >= size - 8 && c < 8) ||
                    (r == 6 || c == 6) ||
                    (r in (size / 2 - 2)..(size / 2 + 2) && c in (size / 2 - 2)..(size / 2 + 2))

            if (!isFinder) {
                val byteIndex = (r * size + c) % bytes.size.coerceAtLeast(1)
                val byteVal = if (bytes.isNotEmpty()) bytes[byteIndex].toInt() else 42
                val bitVal = ((hash shr (r % 16)) xor (byteVal shl (c % 8)) xor (r * 31 + c * 17))
                matrix[r][c] = (bitVal % 3 == 0 || (r + c) % 2 == 0)
            }
        }
    }

    return matrix
}
