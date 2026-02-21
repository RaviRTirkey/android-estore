package com.learning.e_store.screen

import android.R.attr.singleLine
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.learning.e_store.viewmodel.CartViewModel
import com.learning.e_store.viewmodel.CheckoutViewModel
import java.math.BigDecimal

@Composable
fun CheckoutScreenRoute(
    cartViewModel: CartViewModel = hiltViewModel(),
    navController: NavHostController
){
    val cartUiState by cartViewModel.uiState.collectAsState()
    
    val total = cartUiState.cartItems.sumOf { item ->
        val price = item.productPrice.toBigDecimal()
        val quantity = item.quantity.toBigDecimal()
        price * quantity
    }
    
    CheckoutScreen(
        cartTotal = total,
        onBackClick = {navController.popBackStack()},
        onOrderSuccess = {navController.navigate("home"){
            popUpTo("checkout") { inclusive = true }
        } }
    )
    
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    cartTotal: BigDecimal, 
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (state.isOrderPlaced) {
            // Success View
            OrderSuccessView(onHomeClick = onOrderSuccess)
        } else {
            // Form View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Order Summary Card
                OrderSummaryCard(total = cartTotal)

                // 2. Shipping Form
                Text("Shipping Address", style = MaterialTheme.typography.titleMedium)

                CheckoutTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Full Name",
                    error = state.nameError
                )
                CheckoutTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Phone Number",
                    error = state.phoneError
                )
                CheckoutTextField(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    label = "Address",
                    error = state.addressError
                )
                CheckoutTextField(
                    value = state.state,
                    onValueChange = viewModel::onStateChange,
                    label = "State",
                    error = state.stateError
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutTextField(
                        value = state.city,
                        onValueChange = viewModel::onCityChange,
                        label = "Town/City",
                        modifier = Modifier.weight(1f),
                        error = state.cityError
                    )
                    CheckoutTextField(
                        value = state.pincode,
                        onValueChange = viewModel::onPincodeChange,
                        label = "Pincode",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        error = state.pincodeError
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Payment Method Placeholder
                Text("Payment Method", style = MaterialTheme.typography.titleMedium)
                PaymentMethodSelector()

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Place Order Button
                Button(
                    onClick = { viewModel.placeOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading && state.isFormValid
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Place Order - $${"%.2f".format(cartTotal)}")
                    }
                }
            }
        }
    }
}


@Composable
fun OrderSummaryCard(total: BigDecimal) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Amount", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "$${"%.2f".format(total)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier){
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun PaymentMethodSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = true, onClick = {})
        Spacer(modifier = Modifier.width(8.dp))
        Text("Credit Card (Visa ending in 4242)")
    }
}

@Composable
fun OrderSuccessView(onHomeClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50), // Green color
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Order Placed!", style = MaterialTheme.typography.headlineLarge)
        Text("Thank you for your purchase.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onHomeClick) {
            Text("Back to Home")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    // We create a mocked version of the screen that doesn't rely on the ViewModel logic
    MaterialTheme {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Checkout") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrderSummaryCard(total = BigDecimal(499.9))

                Text("Shipping Address", style = MaterialTheme.typography.titleMedium)
                CheckoutTextField(value = "John Doe", onValueChange = {}, label = "Full Name")
                CheckoutTextField(value = "8978972389", onValueChange = {}, label = "Phone Number")
                CheckoutTextField(value = "123 Compose Lane", onValueChange = {}, label = "Address")
                CheckoutTextField(value = "Jharkhand", onValueChange = {}, label = "State")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CheckoutTextField(value = "Ranchi", onValueChange = {}, label = "Town/City", modifier = Modifier.weight(1f))
                    CheckoutTextField(value = "834001", onValueChange = {}, label = "Pincode", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Payment Method", style = MaterialTheme.typography.titleMedium)
                PaymentMethodSelector()

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Place Order - $499.99")
                }
            }
        }
    }
}