package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FoodItem
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.VegGreen
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun AdminMenuManagementScreen(
    adminViewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val foodItems by adminViewModel.allFoodItems.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<FoodItem?>(null) }
    var deletingItem by remember { mutableStateOf<FoodItem?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AmberPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("admin_add_food_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Food Item")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Canteen Food Inventory",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${foodItems.size} active menu items",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(foodItems, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.iconEmoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (item.isVeg) "🥦" else "🍗",
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "${item.category} • ₹${item.price.toInt()} • ⏱️${item.prepTimeMinutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Stock Availability Switch
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Switch(
                                checked = item.isAvailable,
                                onCheckedChange = { adminViewModel.toggleFoodAvailability(item) },
                                colors = SwitchDefaults.colors(checkedThumbColor = AmberPrimary, checkedTrackColor = AmberPrimary.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("toggle_stock_${item.id}")
                            )
                            Text(
                                text = if (item.isAvailable) "In Stock" else "Sold Out",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isAvailable) VegGreen else Color.Gray
                            )
                        }

                        // Edit Button
                        IconButton(
                            onClick = { editingItem = item },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AmberPrimary, modifier = Modifier.size(18.dp))
                        }

                        // Delete Button
                        IconButton(
                            onClick = { deletingItem = item },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Add Food Item Modal
    if (showAddDialog) {
        FoodItemFormDialog(
            title = "Add New Food to Menu",
            initialItem = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, desc, cat, price, prepTime, isVeg, emoji ->
                adminViewModel.addNewFoodItem(name, desc, cat, price, prepTime, isVeg, emoji)
                showAddDialog = false
            }
        )
    }

    // Edit Food Item Modal
    editingItem?.let { item ->
        FoodItemFormDialog(
            title = "Edit ${item.name}",
            initialItem = item,
            onDismiss = { editingItem = null },
            onSave = { name, desc, cat, price, prepTime, isVeg, emoji ->
                adminViewModel.updateFoodItem(
                    item.copy(
                        name = name,
                        description = desc,
                        category = cat,
                        price = price,
                        prepTimeMinutes = prepTime,
                        isVeg = isVeg,
                        iconEmoji = emoji
                    )
                )
                editingItem = null
            }
        )
    }

    // Delete Confirmation
    deletingItem?.let { item ->
        Dialog(onDismissRequest = { deletingItem = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Remove ${item.name}?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Are you sure you want to remove this dish from the canteen menu?",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { deletingItem = null }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                adminViewModel.deleteFoodItem(item)
                                deletingItem = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                        ) {
                            Text("Delete Dish")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemFormDialog(
    title: String,
    initialItem: FoodItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String, cat: String, price: Double, prepTime: Int, isVeg: Boolean, emoji: String) -> Unit
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var desc by remember { mutableStateOf(initialItem?.description ?: "") }
    var cat by remember { mutableStateOf(initialItem?.category ?: "Snacks") }
    var priceStr by remember { mutableStateOf(initialItem?.price?.toInt()?.toString() ?: "40") }
    var prepStr by remember { mutableStateOf(initialItem?.prepTimeMinutes?.toString() ?: "8") }
    var isVeg by remember { mutableStateOf(initialItem?.isVeg ?: true) }
    var emoji by remember { mutableStateOf(initialItem?.iconEmoji ?: "🍱") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("Emoji") },
                        modifier = Modifier.width(80.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Food Name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Ingredients") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = prepStr,
                        onValueChange = { prepStr = it },
                        label = { Text("Prep Mins") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isVeg,
                        onCheckedChange = { isVeg = it }
                    )
                    Text("Pure Vegetarian (Veg)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = priceStr.toDoubleOrNull() ?: 30.0
                            val prep = prepStr.toIntOrNull() ?: 5
                            onSave(name, desc, cat, price, prep, isVeg, emoji)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                    ) {
                        Text("Save Dish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
