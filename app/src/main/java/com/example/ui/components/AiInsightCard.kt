package com.example.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DemandPrediction
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
fun AiInsightCard(
    prediction: DemandPrediction,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NaturalBorderLight, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NaturalSageContainer)
                            .border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(prediction.iconEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = prediction.foodName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkHeading
                        )
                        Text(
                            text = "${prediction.category} • Slot: ${prediction.targetMealSlot.take(18)}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedEarth
                        )
                    }
                }

                // Confidence badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalSageContainer,
                    modifier = Modifier.border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Confidence",
                            tint = NaturalSagePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${prediction.confidenceScore}% Confidence",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NaturalSagePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Forecast stats comparison
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NaturalSandstone)
                    .border(1.dp, NaturalSandstoneBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Predicted Demand",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedEarth
                    )
                    Text(
                        text = "${prediction.predictedQuantity} units",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NaturalSagePrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(NaturalSandstoneBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Historical Avg",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedEarth
                    )
                    Text(
                        text = "${prediction.historicalAverage} units",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeading
                    )
                }

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(NaturalSandstoneBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Demand Shift",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedEarth
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isPositive = prediction.changePercentage >= 0
                        Icon(
                            imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (isPositive) NaturalSagePrimary else Color(0xFFBA1A1A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${prediction.changePercentage}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPositive) NaturalSagePrimary else Color(0xFFBA1A1A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Actionable Recommendation Note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaturalSageContainer)
                    .border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "💡 AI Tip: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = NaturalSagePrimary
                )
                Text(
                    text = prediction.recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDeepOchre
                )
            }
        }
    }
}
