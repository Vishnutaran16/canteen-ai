package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val foodItemId: Long,
    val foodName: String,
    val unitPrice: Double,
    val quantity: Int,
    val iconEmoji: String = "🍱",
    val customizationNote: String = ""
)
