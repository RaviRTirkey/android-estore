package com.learning.e_store.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.learning.e_store.R
import com.learning.e_store.viewmodel.AuthViewModel
import com.learning.e_store.viewmodel.ProfileViewModel
import com.learning.e_store.viewmodel.UiEvent
import com.learning.e_store.viewmodel.UserProfile

@Composable
fun ProfileScreenRoute(
    contentPadding: PaddingValues,
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
){
    val user by profileViewModel.userState.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let{profileViewModel.uploadImage(it)}}
    
    ProfileScreen(
        paddingValues = contentPadding,
        user = user,
        onAddPhotoClick = {launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},
        onMyOrderClick = {navController.navigate("orders")},
        onLogoutClick = authViewModel::logout
    )

    LaunchedEffect(authViewModel) {
        authViewModel.events.collect { event ->
            when (event) {
                UiEvent.NavigateToAuth -> {
                    navController.navigate("auth")
                    {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }

                else -> {navController.navigate("home")}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    user: UserProfile,
    onAddPhotoClick: () -> Unit,
    onMyOrderClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        //Header Section (Avatar + Info)
        item {
            ProfileHeader(user, onAddPhotoClick)
        }

        //Menu Options
        item {
            Text(
                "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ProfileMenuItem(icon = Icons.Default.ShoppingCart, title = "My Orders") {onMyOrderClick()}
            ProfileMenuItem(icon = Icons.Default.Favorite, title = "Wishlist") {}
            ProfileMenuItem(icon = Icons.Default.LocationOn, title = "Shipping Addresses") {}
            ProfileMenuItem(icon = Icons.Default.Settings, title = "Settings") {}
        }

        //Logout Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- Helper Components ---

@Composable
fun ProfileHeader(user: UserProfile, onAddPhotoClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.clickable { onAddPhotoClick() }
        ) {
            val painter = rememberAsyncImagePainter(
            model = user.profileImage.takeIf { it.isNotEmpty() } ?: R.drawable.placeholder_profile
        )

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            if (user.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }

            // Small edit badge
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.size(32.dp).offset(x = (-3).dp, y = (-4).dp),
                shadowElevation = 2.dp
            ) {
                Icon(
                    imageVector = Icons.Default.CameraEnhance,
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            paddingValues = PaddingValues(),
            user = UserProfile(),
            onAddPhotoClick = {},
            onMyOrderClick = {},
            onLogoutClick = {}
        
        )
    }
}