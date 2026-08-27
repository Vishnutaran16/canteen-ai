package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.NaturalAvatarBg
import com.example.ui.theme.NaturalAvatarBorder
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
import com.example.ui.theme.WarningOrange
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    onNavigateToKitchen: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by adminViewModel.dashboardStats.collectAsStateWithLifecycle()
    val allOrders by adminViewModel.allOrders.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Natural Tones Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NaturalBorderLight, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CANTEEN ADMIN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = TextMutedEarth
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Smart Insights",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDarkHeading
                        )
                        Text(
                            text = "Chef Ramesh • Central Operations",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedEarth
                        )
                    }

                    // Avatar badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(NaturalAvatarBg)
                            .border(1.dp, NaturalAvatarBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDeepOchre
                        )
                    }
                }
            }
        }

        // Hero AI Demand Prediction Card (Sage Green container matching natural tones HTML)
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = NaturalSageContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NaturalSageBorder, RoundedCornerShape(28.dp))
                    .clickable(onClick = onNavigateToAi)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NaturalSagePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Demand Prediction",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkHeading
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = NaturalSagePrimary
                        ) {
                            Text(
                                text = "Live",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "High demand expected for Masala Dosa during the 1:00 PM lunch rush (+22% surge). Recommended prep: 65 batches.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDeepOchre,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "Waste Red.: 16.4 kg/day",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NaturalSagePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onNavigateToAi)
                        ) {
                            Text(
                                text = "View Analytics",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NaturalSagePrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = NaturalSagePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Key Metrics: Today's Revenue & Total Orders (Sandstone Card matching HTML spec)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Revenue Card (Sandstone)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NaturalSandstone),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NaturalSandstoneBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S SALES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextMutedEarth
                            )
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                tint = NaturalSagePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "₹${stats.todayRevenue.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDarkHeading
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+18% vs yesterday",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = NaturalSagePrimary
                        )
                    }
                }

                // Total Orders Card (Sandstone)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NaturalSandstone),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NaturalSandstoneBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL ORDERS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextMutedEarth
                            )
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TextMutedEarth,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${stats.todayTotalOrders}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDarkHeading
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Token sequence active",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedEarth
                        )
                    }
                }
            }
        }

        // Live Kitchen Pipeline State Badges
        item {
            Text(
                text = "Live Kitchen Queue State",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeading
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending
                QueueStatusPill(
                    count = stats.pendingOrdersCount,
                    label = "Pending",
                    color = WarningOrange,
                    icon = Icons.Default.PendingActions,
                    modifier = Modifier.weight(1f)
                )

                // Cooking
                QueueStatusPill(
                    count = stats.preparingOrdersCount,
                    label = "Cooking",
                    color = NaturalSagePrimary,
                    icon = Icons.Default.OutdoorGrill,
                    modifier = Modifier.weight(1f)
                )

                // Ready
                QueueStatusPill(
                    count = stats.readyOrdersCount,
                    label = "Ready",
                    color = SuccessGreen,
                    icon = Icons.Default.NotificationsActive,
                    modifier = Modifier.weight(1f)
                )

                // Completed
                QueueStatusPill(
                    count = stats.completedOrdersCount,
                    label = "Handed",
                    color = TextMutedEarth,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Operational Shortcuts Grid
        item {
            Text(
                text = "Staff Action Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeading
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShortcutCard(
                        title = "Kitchen KDS Queue",
                        subtitle = "Manage & Cook live orders",
                        icon = Icons.Default.OutdoorGrill,
                        color = NaturalSagePrimary,
                        onClick = onNavigateToKitchen,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_kitchen_btn")
                    )

                    ShortcutCard(
                        title = "QR Scanner",
                        subtitle = "Verify student token",
                        icon = Icons.Default.QrCodeScanner,
                        color = TextDeepOchre,
                        onClick = onNavigateToScanner,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_scanner_btn")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShortcutCard(
                        title = "Menu & Stock",
                        subtitle = "Toggle sold-out items",
                        icon = Icons.Default.Fastfood,
                        color = WarningOrange,
                        onClick = onNavigateToMenu,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_menu_btn")
                    )

                    ShortcutCard(
                        title = "Sales Analytics",
                        subtitle = "Weekly charts & peak hours",
                        icon = Icons.Default.TrendingUp,
                        color = NaturalSagePrimary,
                        onClick = onNavigateToAi,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shortcut_sales_btn")
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueStatusPill(
    count: Int,
    label: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NaturalSandstone,
        modifier = modifier.border(1.dp, NaturalSandstoneBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextDarkHeading
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .border(1.dp, NaturalBorderLight, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeading
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedEarth
            )
        }
    }
}
