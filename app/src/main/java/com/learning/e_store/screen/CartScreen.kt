package com.learning.e_store.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.learning.e_store.data.remote.api.model.CartItem
import com.learning.e_store.viewmodel.CartUiState
import com.learning.e_store.viewmodel.CartViewModel
import java.math.BigDecimal

@Composable
fun CartRoute(
    contentPadding: PaddingValues,
    cartViewModel: CartViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by cartViewModel.uiState.collectAsState()

    CartScreen(
        uiState = uiState,
        contentPadding = contentPadding,
        onCheckoutClick = {navController.navigate("checkout")},
        onIncrease = {productId, quantity ->
            cartViewModel.updateItemQuantity(productId, quantity + 1)
        },
        onDecrease = { productId, quantity ->
           cartViewModel.updateItemQuantity(productId, quantity - 1) 
        },
        onDelete = { productId -> cartViewModel.deleteItem(productId) }
    )

    LaunchedEffect(Unit) {
        cartViewModel.getCart()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    uiState: CartUiState,
    contentPadding: PaddingValues,
    onCheckoutClick: () -> Unit,
    onIncrease: (Long, Int) -> Unit,
    onDecrease: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        if (uiState.cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your Cart is Empty", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        text = "My Cart",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(uiState.cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = {onIncrease(item.productId, item.quantity)},
                        onDecrease = {onDecrease(item.productId, item.quantity)},
                        onDelete = { onDelete(item.productId) }
                    )
                }

            }
            CartSummary(
                total = uiState.cartItems.sumOf { item -> 
                    val price = item.productPrice.toBigDecimal()
                    val quantity = item.quantity.toBigDecimal()
                    price * quantity
                },
                onCheckout = onCheckoutClick
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = item.productImage,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "$${item.productPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quantity Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (item.quantity == 1) {
                        onDelete()
                    } else {
                        onDecrease()
                    }
                }) {

                    Icon(
                        if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Decrease"
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}

@Composable
fun CartSummary(total: BigDecimal, onCheckout: () -> Unit) {
    Surface(
        shadowElevation = 16.dp,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "$${"%.2f".format(total)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

