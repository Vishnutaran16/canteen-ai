package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SalesRecord
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalSagePrimary
import com.example.ui.theme.NaturalSandstone
import com.example.ui.theme.NaturalSandstoneBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDarkBark
import com.example.ui.theme.TextDarkHeading
import com.example.ui.theme.TextDeepOchre
import com.example.ui.theme.TextMutedEarth

@Composable
fun WeeklySalesBarChart(
    sales: List<SalesRecord>,
    modifier: Modifier = Modifier
) {
    if (sales.isEmpty()) return

    val maxRevenue = remember(sales) {
        sales.maxOfOrNull { it.totalRevenue }?.coerceAtLeast(1000.0) ?: 10000.0
    }

    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(sales) {
        progressAnim.snapTo(0f)
        progressAnim.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NaturalBorderLight, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Revenue & Volume",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeading
                    )
                    Text(
                        text = "7-Day Sales Trend (₹)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedEarth
                    )
                }
                Text(
                    text = "Total ₹${sales.sumOf { it.totalRevenue }.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = NaturalSagePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height - 30f // Leave space for labels
                    val barSpacing = width / sales.size
                    val barWidth = barSpacing * 0.55f

                    // Grid reference lines
                    for (i in 1..3) {
                        val y = height * (i / 4f)
                        drawLine(
                            color = NaturalBorderLight,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.5f
                        )
                    }

                    sales.forEachIndexed { index, record ->
                        val barHeight = ((record.totalRevenue / maxRevenue) * height * progressAnim.value).toFloat()
                        val x = index * barSpacing + (barSpacing - barWidth) / 2f
                        val y = height - barHeight

                        // Sage Natural Gradient Bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(NaturalSagePrimary, Color(0xFF6B9951))
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }
            }

            // Day Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sales.forEach { record ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.dayName.take(3),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = TextMutedEarth
                        )
                        Text(
                            text = "${record.totalOrders}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = NaturalSagePrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeakHoursHeatmap(
    modifier: Modifier = Modifier
) {
    val timeSlots = listOf(
        Pair("08:00 AM - 10:00 AM (Breakfast)", 78),
        Pair("10:00 AM - 12:00 PM (Mid-Morning)", 35),
        Pair("12:00 PM - 02:00 PM (Lunch Rush)", 98),
        Pair("02:00 PM - 04:00 PM (Afternoon)", 25),
        Pair("04:00 PM - 05:30 PM (Evening Snacks)", 92)
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NaturalBorderLight, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Peak Canteen Footfall & Ordering Hours",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeading
            )
            Text(
                text = "AI analyzes student timetable breaks for optimal staffing",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedEarth
            )

            Spacer(modifier = Modifier.height(14.dp))

            timeSlots.forEach { (slot, intensity) ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextDarkHeading
                        )
                        Text(
                            text = if (intensity > 85) "🔥 High Surge ($intensity%)" else if (intensity > 50) "⚡ Moderate ($intensity%)" else "Normal ($intensity%)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (intensity > 85) NaturalSagePrimary else if (intensity > 50) TextDeepOchre else TextMutedEarth
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(NaturalSandstone)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(intensity / 100f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (intensity > 85) NaturalSagePrimary else if (intensity > 50) Color(0xFF6B9951) else NaturalSageBorder
                                )
                        )
                    }
                }
            }
        }
    }
}
