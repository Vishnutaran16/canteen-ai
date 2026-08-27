package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.FoodItem
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.User
import com.example.data.repository.CartItem
import com.example.data.repository.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: CanteenRepository) : ViewModel() {

    // === Menu & Filter State ===
    val allFoodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _vegOnlyFilter = MutableStateFlow(false)
    val vegOnlyFilter: StateFlow<Boolean> = _vegOnlyFilter.asStateFlow()

    // Filtered Menu
    val filteredMenu: StateFlow<List<FoodItem>> = combine(
        allFoodItems,
        _searchQuery,
        _selectedCategory,
        _vegOnlyFilter
    ) { items, query, category, vegOnly ->
        items.filter { item ->
            val matchesCategory = (category == "All" || item.category.equals(category, ignoreCase = true))
            val matchesSearch = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.tags.contains(query, ignoreCase = true)
            val matchesVeg = !vegOnly || item.isVeg
            matchesCategory && matchesSearch && matchesVeg
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Cart State ===
    private val _cartItems = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<Long, CartItem>> = _cartItems.asStateFlow()

    val cartTotalCount: StateFlow<Int> = _cartItems.combine(_cartItems) { map, _ ->
        map.values.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartTotalPrice: StateFlow<Double> = _cartItems.combine(_cartItems) { map, _ ->
        map.values.sumOf { it.foodItem.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // === Checkout Preferences ===
    private val _selectedPickupSlot = MutableStateFlow("In 15 Mins (Fast Pickup)")
    val selectedPickupSlot: StateFlow<String> = _selectedPickupSlot.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("UPI") // "UPI", "CASH", "WALLET"
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes: StateFlow<String> = _orderNotes.asStateFlow()

    // === Active Order Tracker ===
    private val _lastPlacedOrderId = MutableStateFlow<Long?>(1L) // Default to demo active order 1
    val lastPlacedOrderId: StateFlow<Long?> = _lastPlacedOrderId.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setVegOnlyFilter(enabled: Boolean) {
        _vegOnlyFilter.value = enabled
    }

    fun setPickupSlot(slot: String) {
        _selectedPickupSlot.value = slot
    }

    fun setPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun setOrderNotes(notes: String) {
        _orderNotes.value = notes
    }

    // === Cart Operations ===
    fun addToCart(foodItem: FoodItem, customizationNote: String = "") {
        val current = _cartItems.value.toMutableMap()
        val existing = current[foodItem.id]
        if (existing != null) {
            current[foodItem.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[foodItem.id] = CartItem(foodItem = foodItem, quantity = 1, customizationNote = customizationNote)
        }
        _cartItems.value = current
    }

    fun increaseQuantity(foodItemId: Long) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[foodItemId] ?: return
        current[foodItemId] = existing.copy(quantity = existing.quantity + 1)
        _cartItems.value = current
    }

    fun decreaseQuantity(foodItemId: Long) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[foodItemId] ?: return
        if (existing.quantity > 1) {
            current[foodItemId] = existing.copy(quantity = existing.quantity - 1)
        } else {
            current.remove(foodItemId)
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    // === Place Order ===
    fun placeOrder(user: User, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val items = _cartItems.value.values.toList()
            if (items.isEmpty()) return@launch

            val orderId = repository.placeOrder(
                user = user,
                cartItems = items,
                pickupSlot = _selectedPickupSlot.value,
                paymentMethod = _selectedPaymentMethod.value
            )
            _lastPlacedOrderId.value = orderId
            clearCart()
            onComplete(orderId)
        }
    }

    // === Cancel Order ===
    fun cancelOrder(orderId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.cancelOrder(orderId)
            onResult(success)
        }
    }

    // === Feedback ===
    fun submitFeedback(orderId: Long, studentName: String, rating: Int, comment: String, tags: String) {
        viewModelScope.launch {
            repository.submitFeedback(orderId, studentName, rating, comment, tags)
        }
    }

    // === Wallet Top-Up ===
    fun topUpWallet(studentId: String, amount: Double) {
        viewModelScope.launch {
            repository.topUpWallet(studentId, amount)
        }
    }

    fun getStudentOrders(studentId: String): StateFlow<List<Order>> {
        return repository.getOrdersForStudent(studentId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getOrderFlow(orderId: Long): StateFlow<Order?> {
        return repository.getOrderByIdFlow(orderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun getNotifications(studentId: String): StateFlow<List<NotificationItem>> {
        return repository.getNotifications(studentId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun markNotificationsRead(studentId: String) {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(studentId)
        }
    }

    fun selectTrackedOrder(orderId: Long) {
        _lastPlacedOrderId.value = orderId
    }
}
