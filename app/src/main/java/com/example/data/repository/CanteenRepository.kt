package com.example.data.repository

import com.example.data.local.CanteenDao
import com.example.data.model.DemandPrediction
import com.example.data.model.FeedbackItem
import com.example.data.model.FoodItem
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.SalesRecord
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int = 1,
    val customizationNote: String = ""
)

class CanteenRepository(private val dao: CanteenDao) {

    // === USER ===
    suspend fun getUserByStudentId(studentId: String): User? = dao.getUserByStudentId(studentId)
    suspend fun getFirstUserByRole(role: String): User? = dao.getFirstUserByRole(role)
    suspend fun registerUser(user: User): Long = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)

    // === MENU ===
    fun getAllFoodItems(): Flow<List<FoodItem>> = dao.getAllFoodItems()
    fun getAvailableFoodItems(): Flow<List<FoodItem>> = dao.getAvailableFoodItems()
    suspend fun getFoodItemById(id: Long): FoodItem? = dao.getFoodItemById(id)
    suspend fun addFoodItem(foodItem: FoodItem): Long = dao.insertFoodItem(foodItem)
    suspend fun updateFoodItem(foodItem: FoodItem) = dao.updateFoodItem(foodItem)
    suspend fun deleteFoodItem(foodItem: FoodItem) = dao.deleteFoodItem(foodItem)
    suspend fun setFoodItemAvailability(id: Long, isAvailable: Boolean) = dao.setFoodItemAvailability(id, isAvailable)
    suspend fun updateFoodPrice(id: Long, price: Double) = dao.updateFoodPrice(id, price)

    // === ORDERS ===
    fun getAllOrders(): Flow<List<Order>> = dao.getAllOrders()
    fun getOrdersForStudent(studentId: String): Flow<List<Order>> = dao.getOrdersForStudent(studentId)
    fun getOrderByIdFlow(orderId: Long): Flow<Order?> = dao.getOrderByIdFlow(orderId)
    suspend fun getOrderById(orderId: Long): Order? = dao.getOrderById(orderId)
    suspend fun getOrderByNumberOrToken(tokenOrId: String): Order? = dao.getOrderByNumberOrToken(tokenOrId)
    suspend fun getOrderByQrPayload(qrPayload: String): Order? = dao.getOrderByQrPayload(qrPayload)

    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItem>> = dao.getOrderItemsForOrder(orderId)
    suspend fun getOrderItemsSync(orderId: Long): List<OrderItem> = dao.getOrderItemsForOrderSync(orderId)

    // Place an Order
    suspend fun placeOrder(
        user: User,
        cartItems: List<CartItem>,
        pickupSlot: String,
        paymentMethod: String,
        counterNumber: String = "Counter 1"
    ): Long {
        val totalAmount = cartItems.sumOf { it.foodItem.price * it.quantity }
        val randomDigits = Random.nextInt(1000, 9999)
        val orderNumber = "SC-$randomDigits"
        val tokenNumber = "T-${Random.nextInt(10, 99)}"
        val qrPayload = "ORDER:$orderNumber:$tokenNumber:${user.studentId}:${totalAmount}"

        val order = Order(
            orderNumber = orderNumber,
            tokenNumber = tokenNumber,
            studentId = user.studentId,
            studentName = user.name,
            studentPhone = user.phone,
            pickupSlot = pickupSlot,
            status = OrderStatus.PENDING.name,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "CASH") "PAY_ON_PICKUP" else "PAID",
            qrPayload = qrPayload,
            createdAt = System.currentTimeMillis(),
            estimatedPickupTimestamp = System.currentTimeMillis() + (15 * 60 * 1000),
            counterNumber = counterNumber
        )

        val orderId = dao.insertOrder(order)

        val orderItems = cartItems.map {
            OrderItem(
                orderId = orderId,
                foodItemId = it.foodItem.id,
                foodName = it.foodItem.name,
                unitPrice = it.foodItem.price,
                quantity = it.quantity,
                iconEmoji = it.foodItem.iconEmoji,
                customizationNote = it.customizationNote
            )
        }
        dao.insertOrderItems(orderItems)

        // Deduct from wallet if wallet payment
        if (paymentMethod == "WALLET" && user.walletBalance >= totalAmount) {
            val updatedUser = user.copy(walletBalance = user.walletBalance - totalAmount)
            dao.updateUser(updatedUser)
        }

        // Create student notification
        dao.insertNotification(
            NotificationItem(
                userId = user.studentId,
                title = "Order Placed Successfully! 🛒",
                message = "Your Order #$orderNumber (Token: $tokenNumber) for ₹$totalAmount was received. Pickup time: $pickupSlot.",
                orderId = orderId,
                type = "ORDER_UPDATE"
            )
        )

        // Create Admin notification
        dao.insertNotification(
            NotificationItem(
                userId = "ADMIN",
                title = "New Incoming Order! 🛎️",
                message = "New order $orderNumber by ${user.name} (${cartItems.size} items, ₹$totalAmount)",
                orderId = orderId,
                type = "ORDER_UPDATE"
            )
        )

        return orderId
    }

    // Update order status with auto notifications
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Boolean {
        val order = dao.getOrderById(orderId) ?: return false
        val completedTimestamp = if (newStatus == OrderStatus.COMPLETED) System.currentTimeMillis() else null
        dao.updateOrderStatus(orderId, newStatus.name, completedTimestamp)

        val statusMessage = when (newStatus) {
            OrderStatus.ACCEPTED -> "Your order #${order.orderNumber} has been ACCEPTED by the kitchen team."
            OrderStatus.PREPARING -> "Chef Ramesh is PREPARING your order #${order.orderNumber} fresh! Almost ready."
            OrderStatus.READY -> "Order #${order.orderNumber} (Token ${order.tokenNumber}) is READY for pickup at ${order.counterNumber}! 🔔"
            OrderStatus.COMPLETED -> "Order #${order.orderNumber} has been COLLECTED. Enjoy your meal! Please rate us."
            OrderStatus.CANCELLED -> "Order #${order.orderNumber} has been cancelled."
            else -> "Order #${order.orderNumber} status updated to ${newStatus.name}"
        }

        dao.insertNotification(
            NotificationItem(
                userId = order.studentId,
                title = "Order Update: ${newStatus.name.replace("_", " ")}",
                message = statusMessage,
                orderId = orderId,
                type = "ORDER_UPDATE"
            )
        )

        return true
    }

    // Cancel Order if eligible (only pending/accepted)
    suspend fun cancelOrder(orderId: Long): Boolean {
        val order = dao.getOrderById(orderId) ?: return false
        if (order.status == OrderStatus.PENDING.name || order.status == OrderStatus.ACCEPTED.name) {
            dao.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)

            // Refund wallet if paid with wallet
            if (order.paymentMethod == "WALLET") {
                val student = dao.getUserByStudentId(order.studentId)
                if (student != null) {
                    dao.updateUser(student.copy(walletBalance = student.walletBalance + order.totalAmount))
                }
            }

            dao.insertNotification(
                NotificationItem(
                    userId = order.studentId,
                    title = "Order Cancelled ❌",
                    message = "Order #${order.orderNumber} was cancelled. Any online/wallet payment will be refunded.",
                    orderId = orderId,
                    type = "ORDER_UPDATE"
                )
            )
            return true
        }
        return false
    }

    // Feedback & Rating
    suspend fun submitFeedback(orderId: Long, studentName: String, rating: Int, comment: String, tags: String) {
        dao.updateOrderFeedback(orderId, rating, comment)
        dao.insertFeedback(
            FeedbackItem(
                orderId = orderId,
                studentName = studentName,
                rating = rating,
                comment = comment,
                foodTags = tags
            )
        )
    }

    // Notifications
    fun getNotifications(userId: String): Flow<List<NotificationItem>> = dao.getNotificationsForUser(userId)
    suspend fun markAllNotificationsAsRead(userId: String) = dao.markAllNotificationsAsRead(userId)

    // Analytics & Predictions
    fun getSalesRecords(): Flow<List<SalesRecord>> = dao.getAllSalesRecords()
    fun getPredictions(): Flow<List<DemandPrediction>> = dao.getAllPredictions()
    fun getAllFeedback(): Flow<List<FeedbackItem>> = dao.getAllFeedback()

    // Regenerate AI Predictions based on current orders & time
    suspend fun refreshAiPredictions(dayOffset: Int = 1) {
        val simulatedPredictions = listOf(
            DemandPrediction(
                foodName = "Crispy Masala Dosa",
                category = "Breakfast",
                iconEmoji = "🥞",
                predictedQuantity = 145 + Random.nextInt(-10, 15),
                historicalAverage = 120,
                changePercentage = 20,
                targetMealSlot = "Breakfast (8:00 AM - 10:30 AM)",
                peakTimeWindow = "8:30 AM - 9:15 AM",
                confidenceScore = 97,
                recommendation = "High surge expected due to Monday morning exams. Prepare 20% extra batter and coconut chutney.",
                wasteRiskStatus = "LOW"
            ),
            DemandPrediction(
                foodName = "Special Adrak Elaichi Chai",
                category = "Drinks",
                iconEmoji = "☕",
                predictedQuantity = 220 + Random.nextInt(-15, 20),
                historicalAverage = 185,
                changePercentage = 19,
                targetMealSlot = "Evening Snacks (4:00 PM - 5:30 PM)",
                peakTimeWindow = "4:15 PM - 4:50 PM",
                confidenceScore = 98,
                recommendation = "Continuous queue expected between 4:15 PM and 4:45 PM. Set up quick token express lane.",
                wasteRiskStatus = "LOW"
            ),
            DemandPrediction(
                foodName = "Executive South Indian Thali",
                category = "Lunch",
                iconEmoji = "🍱",
                predictedQuantity = 105 + Random.nextInt(-8, 12),
                historicalAverage = 85,
                changePercentage = 23,
                targetMealSlot = "Lunch (12:30 PM - 2:00 PM)",
                peakTimeWindow = "1:00 PM - 1:35 PM",
                confidenceScore = 95,
                recommendation = "Heavy pre-orders from faculties & students. Batch prepare sambar & fresh curd tubs.",
                wasteRiskStatus = "LOW"
            ),
            DemandPrediction(
                foodName = "Crunchy Veg Samosa",
                category = "Snacks",
                iconEmoji = "🥟",
                predictedQuantity = 175 + Random.nextInt(-10, 15),
                historicalAverage = 150,
                changePercentage = 16,
                targetMealSlot = "Snacks (3:30 PM - 5:30 PM)",
                peakTimeWindow = "4:00 PM - 4:40 PM",
                confidenceScore = 94,
                recommendation = "Prepare two hot batches at 3:45 PM and 4:20 PM to maintain crunchiness and reduce cold waste.",
                wasteRiskStatus = "LOW"
            ),
            DemandPrediction(
                foodName = "Hyderabadi Veg Dum Biryani",
                category = "Lunch",
                iconEmoji = "🍚",
                predictedQuantity = 80 + Random.nextInt(-5, 10),
                historicalAverage = 90,
                changePercentage = -11,
                targetMealSlot = "Lunch (12:00 PM - 2:00 PM)",
                peakTimeWindow = "1:15 PM - 1:45 PM",
                confidenceScore = 92,
                recommendation = "Keep portioning exact at 80 plates to avoid evening shelf spoilage; saves ₹1,200 food loss.",
                wasteRiskStatus = "MODERATE"
            ),
            DemandPrediction(
                foodName = "Thick Cold Coffee",
                category = "Drinks",
                iconEmoji = "🥤",
                predictedQuantity = 92 + Random.nextInt(-5, 12),
                historicalAverage = 72,
                changePercentage = 27,
                targetMealSlot = "All Day (11:00 AM - 5:30 PM)",
                peakTimeWindow = "1:30 PM & 4:30 PM",
                confidenceScore = 96,
                recommendation = "High afternoon temperature forecasted. Pre-freeze 100 glasses and stock vanilla ice cream.",
                wasteRiskStatus = "SURGE_EXPECTED"
            )
        )
        dao.clearPredictions()
        dao.insertPredictions(simulatedPredictions)
    }

    suspend fun topUpWallet(studentId: String, amount: Double) {
        val user = dao.getUserByStudentId(studentId) ?: return
        dao.updateUser(user.copy(walletBalance = user.walletBalance + amount))
        dao.insertNotification(
            NotificationItem(
                userId = studentId,
                title = "Wallet Recharged 💳",
                message = "₹$amount was added to your Smart Canteen wallet. New balance: ₹${user.walletBalance + amount}.",
                type = "ALERT"
            )
        )
    }
}
