package com.learning.e_store.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.learning.e_store.ui.theme.EStoreTheme
import com.learning.e_store.viewmodel.Order
import com.learning.e_store.viewmodel.OrdersUiState
import com.learning.e_store.viewmodel.OrdersViewModel
import java.util.Locale

// 1. ROUTE WRAPPER (Handles ViewModel & Navigation)
@Composable
fun OrdersScreenRoute(
    contentPadding: PaddingValues,
    viewModel: OrdersViewModel = hiltViewModel(),
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    OrdersScreen(
        uiState = uiState,
        modifier = Modifier.padding(contentPadding),
        onOrderClick = { orderId ->
            // navController.navigate("order_detail/$orderId")
        }
    )
}

// 2. STATELESS SCREEN (Takes State, NOT ViewModel - Perfect for Previews)
@Composable
fun OrdersScreen(
    uiState: OrdersUiState,
    modifier: Modifier = Modifier,
    onOrderClick: (Long) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.orders.isEmpty()) {
            Text(
                text = "No orders found.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.orders) { order ->
                    OrderItem(
                        order = order,
                        onClick = { onOrderClick(order.orderId) }
                    )
                }
            }
        }
    }
}

// 3. INDIVIDUAL ITEM COMPONENT
@Composable
fun OrderItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.orderDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.itemsCount} Items",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Indicator with safe locale lowercase
            val statusColor = when (order.status.lowercase(Locale.ROOT)) {
                "delivered" -> Color(0xFF4CAF50) // Green
                "processing" -> Color(0xFFFF9800) // Orange
                "shipped" -> Color(0xFF2196F3) // Blue
                else -> Color.Gray
            }

            Text(
                text = order.status,
                style = MaterialTheme.typography.labelMedium,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// 4. THE PREVIEW (Injects mock state directly into the stateless screen)
@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    EStoreTheme {
        // Mock State with Long IDs
        val mockState = OrdersUiState(
            isLoading = false,
            orders = listOf(
                Order(1001L, "Oct 24, 2023", 120.50, "Delivered", 3),
                Order(1002L, "Nov 02, 2023", 45.99, "Processing", 1),
                Order(1003L, "Nov 15, 2023", 299.00, "Shipped", 2)
            )
        )

        // Render the screen directly with the mock state
        OrdersScreen(
            uiState = mockState,
            onOrderClick = {}
        )
    }
}