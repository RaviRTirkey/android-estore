package com.learning.e_store.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.learning.e_store.data.remote.api.model.CategoryResponse
import com.learning.e_store.viewmodel.CategoriesUiState
import com.learning.e_store.viewmodel.CategoriesViewModel

@Composable
fun CategoriesScreenRoute(
    contentPadding: PaddingValues,
    viewModel: CategoriesViewModel = hiltViewModel(),
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    CategoriesScreen(
        uiState = uiState,
        modifier = Modifier.padding(contentPadding),
        onCategoryClick = { categoryId ->
             navController.navigate("categoryProducts/$categoryId")
        }
    )
}

@Composable
fun CategoriesScreen(
    uiState: CategoriesUiState,
    modifier: Modifier = Modifier,
    onCategoryClick: (Long) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null){
            Text("Error: ${uiState.error}")
        }
        else{
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = uiState.categories,
                    // Make the first item span across both columns for a "Hero" look
                    span = { index, _ ->
                        if (index == 0) GridItemSpan(maxLineSpan) else GridItemSpan(1)
                    }
                ) { index, category ->
                    CategoryCard(
                        category = category,
                        isHero = index == 0, 
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryResponse,
    isHero: Boolean,
    onClick: () -> Unit
) {
    // Hero cards are taller to stand out
    val cardHeight = if (isHero) 220.dp else 160.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark Gradient Overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = category.name,
                    style = if (isHero) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CategoriesScreenPreview() {
    MaterialTheme {
        val mockState = CategoriesUiState(
            isLoading = false,
            categories = listOf(
                CategoryResponse(1L, "New Arrivals", "https://images.unsplash.com/photo-1445205170230-053b83016050?q=80&w=800&auto=format&fit=crop"),
                CategoryResponse(2L, "Electronics", "https://images.unsplash.com/photo-1498049794561-7780e7231661?q=80&w=600&auto=format&fit=crop"),
                CategoryResponse(3L, "Men's Fashion", "https://images.unsplash.com/photo-1617137968427-85924c800a22?q=80&w=600&auto=format&fit=crop"),
                CategoryResponse(4L, "Women's Style", "https://images.unsplash.com/photo-1483985988355-763728e1935b?q=80&w=600&auto=format&fit=crop")
            )
        )

        CategoriesScreen(
            uiState = mockState,
            onCategoryClick = {}
        )
    }
}