package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String, // e.g. "SC-8492"
    val tokenNumber: String, // e.g. "T-18"
    val studentId: String,
    val studentName: String,
    val studentPhone: String = "",
    val pickupSlot: String, // "12:30 PM - 12:45 PM"
    val status: String = OrderStatus.PENDING.name,
    val totalAmount: Double,
    val paymentMethod: String = "UPI", // "UPI", "CASH", "WALLET"
    val paymentStatus: String = "PAID", // "PAID", "PAY_ON_PICKUP"
    val qrPayload: String = "", // Payload string to verify
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedPickupTimestamp: Long = System.currentTimeMillis() + (15 * 60 * 1000),
    val completedAt: Long? = null,
    val rating: Int = 0,
    val feedbackNote: String = "",
    val counterNumber: String = "Counter 2"
)
