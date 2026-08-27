package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DemandPrediction
import com.example.data.model.FeedbackItem
import com.example.data.model.FoodItem
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.SalesRecord
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CanteenDao {

    // === USERS ===
    @Query("SELECT * FROM users WHERE studentId = :studentId LIMIT 1")
    suspend fun getUserByStudentId(studentId: String): User?

    @Query("SELECT * FROM users WHERE role = :role LIMIT 1")
    suspend fun getFirstUserByRole(role: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    // === FOOD ITEMS ===
    @Query("SELECT * FROM food_items ORDER BY category ASC, name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isAvailable = 1 ORDER BY category ASC, name ASC")
    fun getAvailableFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getFoodItemById(id: Long): FoodItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItem>)

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("UPDATE food_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun setFoodItemAvailability(id: Long, isAvailable: Boolean)

    @Query("UPDATE food_items SET price = :newPrice WHERE id = :id")
    suspend fun updateFoodPrice(id: Long, newPrice: Double)

    // === ORDERS ===
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getOrdersForStudent(studentId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt ASC")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderByIdFlow(orderId: Long): Flow<Order?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Long): Order?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber OR tokenNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumberOrToken(orderNumber: String): Order?

    @Query("SELECT * FROM orders WHERE qrPayload = :qrPayload LIMIT 1")
    suspend fun getOrderByQrPayload(qrPayload: String): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status, completedAt = :completedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String, completedAt: Long? = null)

    @Query("UPDATE orders SET rating = :rating, feedbackNote = :feedback WHERE id = :orderId")
    suspend fun updateOrderFeedback(orderId: Long, rating: Int, feedback: String)

    // === ORDER ITEMS ===
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItem>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsForOrderSync(orderId: Long): List<OrderItem>

    @Query("SELECT * FROM order_items")
    fun getAllOrderItems(): Flow<List<OrderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    // === NOTIFICATIONS ===
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: String)

    // === FEEDBACK ===
    @Query("SELECT * FROM feedback ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackItem): Long

    // === SALES & ANALYTICS ===
    @Query("SELECT * FROM sales_records ORDER BY id ASC")
    fun getAllSalesRecords(): Flow<List<SalesRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesRecords(records: List<SalesRecord>)

    // === AI PREDICTIONS ===
    @Query("SELECT * FROM demand_predictions ORDER BY predictedQuantity DESC")
    fun getAllPredictions(): Flow<List<DemandPrediction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<DemandPrediction>)

    @Query("DELETE FROM demand_predictions")
    suspend fun clearPredictions()
}
