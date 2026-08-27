package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiInsightCard
import com.example.ui.components.PeakHoursHeatmap
import com.example.ui.components.WeeklySalesBarChart
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
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminAiPredictionScreen(
    adminViewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val predictions by adminViewModel.demandPredictions.collectAsStateWithLifecycle()
    val sales by adminViewModel.salesRecords.collectAsStateWithLifecycle()
    val isAiGenerating by adminViewModel.isAiGenerating.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = AI Demand Predictions, 1 = Sales & Peak Hours Analytics, 2 = Expo Manifesto

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = NaturalSagePrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NaturalSagePrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NaturalBorderLight)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("AI Forecaster 🤖", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 0) NaturalSagePrimary else TextMutedEarth) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Sales & Trends 📈", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 1) NaturalSagePrimary else TextMutedEarth) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Expo Manifesto 💡", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 2) NaturalSagePrimary else TextMutedEarth) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // AI Demand Forecaster Tab
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSageContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NaturalSageBorder, RoundedCornerShape(24.dp))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NaturalSagePrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "AI Demand Forecaster",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkHeading
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NaturalSagePrimary
                                    ) {
                                        Text(
                                            text = "94% Accuracy",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "AI analyzes lecture timetables, historical order volumes, and peak break times to calculate precise batch cooking requirements.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextDeepOchre
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { adminViewModel.refreshAiDemandPredictions() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NaturalSagePrimary,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("refresh_ai_predictions_btn")
                                ) {
                                    if (isAiGenerating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Recomputing AI Demand...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Regenerate AI Forecast", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Food Waste Savings Highlight
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = NaturalSandstone),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NaturalSandstoneBorder, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Eco, contentDescription = null, tint = NaturalSagePrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("16.4 kg / Day", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = NaturalSagePrimary)
                                    Text("Food Waste Prevented", style = MaterialTheme.typography.labelSmall, color = TextMutedEarth)
                                }

                                Box(modifier = Modifier.height(36.dp).width(1.dp).background(NaturalSandstoneBorder))

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Savings, contentDescription = null, tint = TextDeepOchre, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("₹3,850 / Day", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = TextDarkHeading)
                                    Text("Kitchen Cost Savings", style = MaterialTheme.typography.labelSmall, color = TextMutedEarth)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Item-Wise Predicted Batches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkHeading
                        )
                    }

                    items(predictions, key = { it.id }) { pred ->
                        AiInsightCard(prediction = pred)
                    }
                }

                1 -> {
                    // Sales & Analytics Tab
                    item {
                        WeeklySalesBarChart(sales = sales)
                    }

                    item {
                        PeakHoursHeatmap()
                    }

                    item {
                        // Payment Method Distribution
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NaturalBorderLight, RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Payment Mode Distribution",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkHeading
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⚡ UPI (GPay / PhonePe / Paytm): 68%", color = TextDarkHeading)
                                    Text("₹18,420", fontWeight = FontWeight.Bold, color = NaturalSagePrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("💳 Campus Student Wallet: 24%", color = TextDarkHeading)
                                    Text("₹6,500", fontWeight = FontWeight.Bold, color = TextDarkHeading)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("💵 Cash on Counter: 8%", color = TextDarkHeading)
                                    Text("₹2,160", fontWeight = FontWeight.Bold, color = TextDarkHeading)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Expo Architecture Manifesto Tab
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NaturalBorderLight, RoundedCornerShape(24.dp))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Smart Canteen Management System",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NaturalSagePrimary
                                )
                                Text(
                                    text = "College Student Expo 2026 Project Architecture",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMutedEarth
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ArchitectureBullet(
                                    number = "1",
                                    title = "Digital Pre-Ordering & QR Token System",
                                    description = "Students pre-book meals with custom pickup slots. The app deterministically computes a secure QR code matrix and unique token number for zero-wait pickups."
                                )

                                ArchitectureBullet(
                                    number = "2",
                                    title = "AI-Based Food Demand Prediction",
                                    description = "Solves the chronic college canteen overproduction/shortage problem by predicting meal demand per timetable slot, reducing daily food wastage by up to 16.4 kg."
                                )

                                ArchitectureBullet(
                                    number = "3",
                                    title = "Real-Time Kitchen Display System (KDS)",
                                    description = "Provides canteen staff with a synchronized workflow pipeline (Pending -> Accepted -> Preparing -> Ready -> Completed)."
                                )

                                ArchitectureBullet(
                                    number = "4",
                                    title = "Student Campus Wallet & Instant UPI",
                                    description = "Integrated cashless campus payment options with instant wallet top-ups and digitized transaction receipts."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchitectureBullet(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = NaturalSagePrimary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeading
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedEarth,
                lineHeight = 18.sp
            )
        }
    }
}
