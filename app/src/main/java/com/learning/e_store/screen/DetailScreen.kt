package com.learning.e_store.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.learning.e_store.data.remote.api.model.Product
import com.learning.e_store.viewmodel.CartUiEvent
import com.learning.e_store.viewmodel.CartViewModel
import com.learning.e_store.viewmodel.DetailViewModel
import com.learning.e_store.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    product: Product?,
    onAddToCartClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // "Add to Cart"
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = onAddToCartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Add to Cart - $${product?.displayPrice ?: "0.00"}")
                }
            }
        }
    ) { paddingValues ->
        // Main Content
        product?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // Large Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray) // Placeholder color
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Product Info Section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${item.displayPrice}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.productDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } ?: run {
            // Loading
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun DetailScreenRoute(
    navController: NavHostController,
    detailViewModel: DetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by detailViewModel.productState.collectAsState()

    DetailScreen(
        product = uiState.product,
        onBackClick = { navController.popBackStack() },
        onAddToCartClick = { cartViewModel.addToCart(uiState.product?.productId, quantity = 1) }
    )

    LaunchedEffect(cartViewModel) {
        cartViewModel.uiEvent.collect { event ->
            when (event) {
                is CartUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DetailScreenPreview() {
    
    DetailScreen(
        product = Product(
            productName = "Cool Shoes", displayPrice = "99.99",
            productId = 435L,
            productDescription = "TODO()",
            stockQuantity = 32,
            imageUrl = "TODO()",
            category = "TODO()"
        ),
        onAddToCartClick = {},
        onBackClick = {}
    )
}