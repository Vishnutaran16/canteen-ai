package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.User
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainContainer(
    currentUser: User,
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var placedOrderIdForSuccess by remember { mutableStateOf<Long?>(null) }

    val cartCount by studentViewModel.cartTotalCount.collectAsStateWithLifecycle()
    val notifications by studentViewModel.getNotifications(currentUser.studentId).collectAsStateWithLifecycle()
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Smart Canteen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                navigationIcon = {
                    // Fast Switch to Staff / Admin button in header
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberPrimary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clickable { authViewModel.switchToAdmin() }
                            .testTag("appbar_switch_admin_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch to Staff",
                                tint = AmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Admin",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary
                            )
                        }
                    }
                },
                actions = {
                    // Wallet Balance Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { selectedTab = 4 }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = AmberPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹${currentUser.walletBalance.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Notification Bell
                    IconButton(
                        onClick = { showNotificationDialog = true },
                        modifier = Modifier.testTag("student_notif_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = ErrorRed) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 0: Menu
                NavigationBarItem(
                    selected = selectedTab == 0 && placedOrderIdForSuccess == null,
                    onClick = { selectedTab = 0; placedOrderIdForSuccess = null },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Menu") },
                    label = { Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_menu_tab")
                )

                // 1: Cart
                NavigationBarItem(
                    selected = selectedTab == 1 && placedOrderIdForSuccess == null,
                    onClick = { selectedTab = 1; placedOrderIdForSuccess = null },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = AmberPrimary) {
                                        Text("$cartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_cart_tab")
                )

                // 2: Live Tracker
                NavigationBarItem(
                    selected = selectedTab == 2 && placedOrderIdForSuccess == null,
                    onClick = { selectedTab = 2; placedOrderIdForSuccess = null },
                    icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Live Tracker") },
                    label = { Text("Track", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_track_tab")
                )

                // 3: History
                NavigationBarItem(
                    selected = selectedTab == 3 && placedOrderIdForSuccess == null,
                    onClick = { selectedTab = 3; placedOrderIdForSuccess = null },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_history_tab")
                )

                // 4: Profile
                NavigationBarItem(
                    selected = selectedTab == 4 && placedOrderIdForSuccess == null,
                    onClick = { selectedTab = 4; placedOrderIdForSuccess = null },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (placedOrderIdForSuccess != null) {
                OrderConfirmationScreen(
                    orderId = placedOrderIdForSuccess ?: 1L,
                    studentViewModel = studentViewModel,
                    onTrackOrder = { ordId ->
                        studentViewModel.selectTrackedOrder(ordId)
                        placedOrderIdForSuccess = null
                        selectedTab = 2
                    },
                    onBackToMenu = {
                        placedOrderIdForSuccess = null
                        selectedTab = 0
                    }
                )
            } else {
                when (selectedTab) {
                    0 -> MenuScreen(
                        studentViewModel = studentViewModel,
                        onNavigateToCart = { selectedTab = 1 }
                    )
                    1 -> CartScreen(
                        studentViewModel = studentViewModel,
                        currentUser = currentUser,
                        onOrderPlaced = { orderId ->
                            placedOrderIdForSuccess = orderId
                        },
                        onNavigateToMenu = { selectedTab = 0 }
                    )
                    2 -> OrderTrackingScreen(
                        studentViewModel = studentViewModel,
                        currentUser = currentUser
                    )
                    3 -> OrderHistoryScreen(
                        studentViewModel = studentViewModel,
                        currentUser = currentUser,
                        onTrackOrder = { orderId ->
                            studentViewModel.selectTrackedOrder(orderId)
                            selectedTab = 2
                        }
                    )
                    4 -> StudentProfileScreen(
                        currentUser = currentUser,
                        authViewModel = authViewModel,
                        studentViewModel = studentViewModel
                    )
                }
            }
        }
    }

    // Notification Dialog
    if (showNotificationDialog) {
        NotificationDialog(
            notifications = notifications,
            onDismiss = { showNotificationDialog = false },
            onMarkAllRead = { studentViewModel.markNotificationsRead(currentUser.studentId) }
        )
    }
}
