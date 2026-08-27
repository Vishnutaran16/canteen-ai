package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DemandPrediction
import com.example.data.model.FeedbackItem
import com.example.data.model.FoodItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.SalesRecord
import com.example.data.repository.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminDashboardStats(
    val todayTotalOrders: Int = 0,
    val todayRevenue: Double = 0.0,
    val pendingOrdersCount: Int = 0,
    val preparingOrdersCount: Int = 0,
    val readyOrdersCount: Int = 0,
    val completedOrdersCount: Int = 0,
    val mostPopularItem: String = "Crispy Masala Dosa",
    val estimatedWasteReductionKg: Double = 16.4
)

class AdminViewModel(private val repository: CanteenRepository) : ViewModel() {

    // All Orders Flow
    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Food Items
    val allFoodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sales Records
    val salesRecords: StateFlow<List<SalesRecord>> = repository.getSalesRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Demand Predictions
    val demandPredictions: StateFlow<List<DemandPrediction>> = repository.getPredictions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Customer Feedback
    val feedbackList: StateFlow<List<FeedbackItem>> = repository.getAllFeedback()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Aggregate Statistics
    val dashboardStats: StateFlow<AdminDashboardStats> = allOrders.map { orders ->
        val totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED.name }.sumOf { it.totalAmount }
        val pending = orders.count { it.status == OrderStatus.PENDING.name }
        val preparing = orders.count { it.status == OrderStatus.PREPARING.name }
        val ready = orders.count { it.status == OrderStatus.READY.name }
        val completed = orders.count { it.status == OrderStatus.COMPLETED.name }

        AdminDashboardStats(
            todayTotalOrders = orders.size,
            todayRevenue = totalRevenue,
            pendingOrdersCount = pending,
            preparingOrdersCount = preparing,
            readyOrdersCount = ready,
            completedOrdersCount = completed,
            mostPopularItem = "Crispy Masala Dosa",
            estimatedWasteReductionKg = 16.4
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminDashboardStats())

    // Order status filter in Kitchen management
    private val _orderStatusFilter = MutableStateFlow("ALL")
    val orderStatusFilter: StateFlow<String> = _orderStatusFilter.asStateFlow()

    // QR Scanner verification state
    private val _scannedVerificationResult = MutableStateFlow<Order?>(null)
    val scannedVerificationResult: StateFlow<Order?> = _scannedVerificationResult.asStateFlow()

    private val _verificationMessage = MutableStateFlow<String?>(null)
    val verificationMessage: StateFlow<String?> = _verificationMessage.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    fun setOrderStatusFilter(status: String) {
        _orderStatusFilter.value = status
    }

    // Advance Order Status
    fun advanceOrderStatus(order: Order, nextStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, nextStatus)
            // If currently viewing verification result, update it
            if (_scannedVerificationResult.value?.id == order.id) {
                _scannedVerificationResult.value = _scannedVerificationResult.value?.copy(status = nextStatus.name)
            }
        }
    }

    // Toggle Availability
    fun toggleFoodAvailability(foodItem: FoodItem) {
        viewModelScope.launch {
            repository.setFoodItemAvailability(foodItem.id, !foodItem.isAvailable)
        }
    }

    // Add New Food Item
    fun addNewFoodItem(
        name: String,
        description: String,
        category: String,
        price: Double,
        prepTime: Int,
        isVeg: Boolean,
        iconEmoji: String
    ) {
        viewModelScope.launch {
            val item = FoodItem(
                name = name,
                description = description,
                category = category,
                price = price,
                prepTimeMinutes = prepTime,
                isVeg = isVeg,
                iconEmoji = iconEmoji.ifBlank { "🍱" },
                stockQuantity = 50,
                isAvailable = true
            )
            repository.addFoodItem(item)
        }
    }

    // Update Food Item
    fun updateFoodItem(item: FoodItem) {
        viewModelScope.launch {
            repository.updateFoodItem(item)
        }
    }

    // Delete Food Item
    fun deleteFoodItem(item: FoodItem) {
        viewModelScope.launch {
            repository.deleteFoodItem(item)
        }
    }

    // QR / Token Verification
    fun verifyQrOrToken(input: String) {
        viewModelScope.launch {
            val clean = input.trim()
            val order = if (clean.startsWith("ORDER:")) {
                repository.getOrderByQrPayload(clean)
            } else {
                repository.getOrderByNumberOrToken(clean)
            }

            if (order != null) {
                _scannedVerificationResult.value = order
                _verificationMessage.value = "Verified! Order #${order.orderNumber} (Token: ${order.tokenNumber}) for ${order.studentName}"
            } else {
                _scannedVerificationResult.value = null
                _verificationMessage.value = "No active order found matching '$clean'."
            }
        }
    }

    fun clearVerification() {
        _scannedVerificationResult.value = null
        _verificationMessage.value = null
    }

    // Refresh AI Demand Prediction
    fun refreshAiDemandPredictions() {
        viewModelScope.launch {
            _isAiGenerating.value = true
            repository.refreshAiPredictions()
            _isAiGenerating.value = false
        }
    }

    fun getOrderItemsForOrder(orderId: Long): StateFlow<List<OrderItem>> {
        return repository.getOrderItemsForOrder(orderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
