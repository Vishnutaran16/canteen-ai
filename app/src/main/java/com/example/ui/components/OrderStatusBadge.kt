package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val statusEnum = try {
        OrderStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        OrderStatus.PENDING
    }

    val (bgColor, textColor, label, showPulse) = when (statusEnum) {
        OrderStatus.PENDING -> Quad(Color(0xFFFFF3E0), WarningOrange, "Pending Approval ⏳", false)
        OrderStatus.ACCEPTED -> Quad(Color(0xFFE1F5FE), InfoBlue, "Accepted 👨‍🍳", false)
        OrderStatus.PREPARING -> Quad(Color(0xFFFFECB3), Color(0xFFE65100), "Preparing 🔥", true)
        OrderStatus.READY -> Quad(Color(0xFFE8F5E9), SuccessGreen, "Ready for Pickup! 🔔", true)
        OrderStatus.COMPLETED -> Quad(Color(0xFFECEFF1), Color(0xFF455A64), "Completed ✨", false)
        OrderStatus.CANCELLED -> Quad(Color(0xFFFFEBEE), ErrorRed, "Cancelled ✖", false)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_badge")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showPulse) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulseAlpha)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
