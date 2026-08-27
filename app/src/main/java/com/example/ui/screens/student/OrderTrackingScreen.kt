package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.User
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun OrderTrackingScreen(
    studentViewModel: StudentViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val orders by studentViewModel.getStudentOrders(currentUser.studentId).collectAsStateWithLifecycle()
    val activeOrders = orders.filter { it.status != OrderStatus.COMPLETED.name && it.status != OrderStatus.CANCELLED.name }

    val trackedOrderId by studentViewModel.lastPlacedOrderId.collectAsStateWithLifecycle()
    val currentOrder = orders.find { it.id == trackedOrderId } ?: activeOrders.firstOrNull() ?: orders.firstOrNull()

    var cancelMessage by remember { mutableStateOf<String?>(null) }

    if (currentOrder == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛵", fontSize = 54.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Active Orders to Track",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Place a food order from the menu to get your live kitchen token.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val orderItems by studentViewModel.getOrderFlow(currentOrder.id).collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active orders selector tabs if multiple
        if (activeOrders.size > 1) {
            item {
                Text(
                    text = "Active Live Orders:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activeOrders) { ord ->
                        FilterChip(
                            selected = ord.id == currentOrder.id,
                            onClick = { studentViewModel.selectTrackedOrder(ord.id) },
                            label = { Text("Token ${ord.tokenNumber} (#${ord.orderNumber})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Header Card with Live Status & QR Code
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${currentOrder.orderNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Pickup: ${currentOrder.pickupSlot}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OrderStatusBadge(status = currentOrder.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code View
                    QrCodeView(
                        payload = currentOrder.qrPayload,
                        size = 160.dp,
                        showScanAnimation = (currentOrder.status == OrderStatus.PREPARING.name || currentOrder.status == OrderStatus.READY.name),
                        tokenNumber = currentOrder.tokenNumber
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberPrimary.copy(alpha = 0.08f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentOrder.status == OrderStatus.READY.name)
                                "🔔 Your meal is ready! Please collect at ${currentOrder.counterNumber}"
                            else
                                "Show this QR Code or Token at ${currentOrder.counterNumber} for quick pickup",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberPrimary
                        )
                    }
                }
            }
        }

        // Stepper Progress Timeline
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Live Preparation Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val steps = listOf(
                        Triple(OrderStatus.PENDING, "Order Placed", "Canteen received advance booking"),
                        Triple(OrderStatus.ACCEPTED, "Order Accepted", "Kitchen queued order for preparation"),
                        Triple(OrderStatus.PREPARING, "Cooking Fresh", "Chef is preparing your meal hot"),
                        Triple(OrderStatus.READY, "Ready at Counter", "Meal is ready for pickup with QR code"),
                        Triple(OrderStatus.COMPLETED, "Collected", "Handed over to student")
                    )

                    val currentStatus = try {
                        OrderStatus.valueOf(currentOrder.status)
                    } catch (e: Exception) {
                        OrderStatus.PENDING
                    }

                    steps.forEachIndexed { index, (stepStatus, stepTitle, stepSubtitle) ->
                        val isDone = currentStatus.ordinal >= stepStatus.ordinal
                        val isCurrent = currentStatus == stepStatus

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) (if (isCurrent) AmberPrimary else SuccessGreen)
                                            else Color.LightGray.copy(alpha = 0.5f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone && !isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) Color.White else Color.DarkGray
                                        )
                                    }
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(30.dp)
                                            .background(
                                                if (currentStatus.ordinal > stepStatus.ordinal) SuccessGreen
                                                else Color.LightGray.copy(alpha = 0.4f)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                Text(
                                    text = stepTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isCurrent) AmberPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stepSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cancel Order Section (If pending/accepted)
        val canCancel = (currentOrder.status == OrderStatus.PENDING.name || currentOrder.status == OrderStatus.ACCEPTED.name)
        if (canCancel) {
            item {
                OutlinedButton(
                    onClick = {
                        studentViewModel.cancelOrder(currentOrder.id) { success ->
                            cancelMessage = if (success) "Order #${currentOrder.orderNumber} was cancelled." else "Could not cancel."
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("cancel_order_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel Order (Before Cooking Starts)", fontWeight = FontWeight.Bold)
                }
            }
        }

        cancelMessage?.let { msg ->
            item {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
