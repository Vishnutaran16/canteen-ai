package com.example.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderStatus
import com.example.data.model.User
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.AmberSecondary
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainContainer(
    currentUser: User,
    authViewModel: AuthViewModel,
    adminViewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val allOrders by adminViewModel.allOrders.collectAsStateWithLifecycle()
    val activeKitchenOrdersCount = allOrders.count {
        it.status == OrderStatus.PENDING.name || it.status == OrderStatus.ACCEPTED.name || it.status == OrderStatus.PREPARING.name
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kitchen & Admin Portal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                navigationIcon = {
                    // Fast Switch to Student button in header
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberSecondary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clickable { authViewModel.switchToStudent() }
                            .testTag("admin_switch_student_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch to Student",
                                tint = AmberSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Student",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberSecondary
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "👨‍🍳 Chef Ramesh",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
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
                // 0: Dashboard
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("admin_nav_dashboard")
                )

                // 1: Kitchen Orders
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activeKitchenOrdersCount > 0) {
                                    Badge(containerColor = AmberPrimary) {
                                        Text("$activeKitchenOrdersCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.OutdoorGrill, contentDescription = "Kitchen Orders")
                        }
                    },
                    label = { Text("Kitchen KDS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("admin_nav_kitchen")
                )

                // 2: Menu Stock
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Fastfood, contentDescription = "Menu") },
                    label = { Text("Menu & Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("admin_nav_menu")
                )

                // 3: QR Scanner
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scanner") },
                    label = { Text("QR Scanner", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("admin_nav_scanner")
                )

                // 4: AI & Sales
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Forecaster") },
                    label = { Text("AI & Sales", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AmberPrimary, indicatorColor = AmberPrimary.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("admin_nav_ai")
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
            when (selectedTab) {
                0 -> AdminDashboardScreen(
                    adminViewModel = adminViewModel,
                    onNavigateToKitchen = { selectedTab = 1 },
                    onNavigateToScanner = { selectedTab = 3 },
                    onNavigateToMenu = { selectedTab = 2 },
                    onNavigateToAi = { selectedTab = 4 }
                )
                1 -> AdminOrderManagementScreen(adminViewModel = adminViewModel)
                2 -> AdminMenuManagementScreen(adminViewModel = adminViewModel)
                3 -> AdminQrScannerScreen(adminViewModel = adminViewModel)
                4 -> AdminAiPredictionScreen(adminViewModel = adminViewModel)
            }
        }
    }
}
