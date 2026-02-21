package com.learning.e_store.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.G
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.learning.e_store.data.remote.api.model.Product
import com.learning.e_store.screen.navigation.NavigateAuth
import com.learning.e_store.viewmodel.AuthViewModel
import com.learning.e_store.viewmodel.ProductViewModel

@Composable
fun HomeScreenRoute(
    contentPadding: PaddingValues,
    authViewModel: AuthViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }
    
    
    HomeScreen(
        modifier = Modifier.padding(contentPadding),
        onProductClick = { productId ->
            navHostController.navigate("detail/$productId")
        },
        onRefresh = productViewModel::onRefresh
    )

    NavigateAuth(
        viewModel = authViewModel,
        navController = navHostController,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()


    PullToRefreshBox (
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(uiState.products){ index, product ->
                    
                    if (index >= uiState.products.size - 1 && !uiState.isLastPage && !uiState.isPaginating){
                        LaunchedEffect(key1 = Unit) {
                            viewModel.loadNextPage()
                        }
                    }
                    
                    ProductItem(product = product, onClick = { onProductClick(product.productId) })
                    
                }

                if (uiState.isPaginating) {
                    item(span = {GridItemSpan(maxLineSpan) }){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }
                }
                
            }
        }
    }

}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Product Image
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // Product Details
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.displayPrice}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

