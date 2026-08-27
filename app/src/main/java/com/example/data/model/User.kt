package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String, // e.g. "CS2026042" or "ADMIN01"
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // "STUDENT" or "ADMIN"
    val walletBalance: Double = 350.0,
    val department: String = "Computer Science & Engg",
    val profileAvatar: String = "🎓"
)
