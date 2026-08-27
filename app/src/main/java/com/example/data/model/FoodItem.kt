package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // "Breakfast", "Lunch", "Snacks", "Drinks", "Chef Specials"
    val price: Double,
    val prepTimeMinutes: Int = 10,
    val calories: Int = 250,
    val isVeg: Boolean = true,
    val isAvailable: Boolean = true,
    val stockQuantity: Int = 50,
    val iconEmoji: String = "🍱",
    val rating: Float = 4.8f,
    val totalOrdersCount: Int = 120,
    val tags: String = "Popular,Quick" // comma separated
)
