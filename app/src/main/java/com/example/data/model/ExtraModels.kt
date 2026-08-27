package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String, // studentId or "ADMIN" or "ALL"
    val title: String,
    val message: String,
    val orderId: Long? = null,
    val type: String = "ORDER_UPDATE", // "ORDER_UPDATE", "OFFER", "ALERT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "feedback")
data class FeedbackItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val studentName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val foodTags: String = "" // "Tasty,Fast Delivery"
)

@Entity(tableName = "sales_records")
data class SalesRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayName: String, // "Monday", "Tuesday", etc.
    val dateLabel: String, // "2026-08-20"
    val totalRevenue: Double,
    val totalOrders: Int,
    val peakHourLabel: String, // "12 PM - 1 PM"
    val topSellingFood: String,
    val wasteReductionKg: Double = 14.5
)

@Entity(tableName = "demand_predictions")
data class DemandPrediction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodName: String,
    val category: String,
    val iconEmoji: String,
    val predictedQuantity: Int,
    val historicalAverage: Int,
    val changePercentage: Int, // e.g. +15%
    val targetMealSlot: String, // "Breakfast (8 AM - 10 AM)", "Lunch (12 PM - 2 PM)", "Evening Snacks (4 PM - 6 PM)"
    val peakTimeWindow: String, // "12:45 PM - 1:30 PM"
    val confidenceScore: Int = 94, // 94%
    val recommendation: String,
    val wasteRiskStatus: String = "LOW" // "LOW", "MODERATE", "SURGE_EXPECTED"
)
