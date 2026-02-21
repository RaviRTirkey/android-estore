package com.learning.e_store.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.learning.e_store.R
import com.learning.e_store.screen.navigation.NavigateAuth
import com.learning.e_store.viewmodel.AuthViewModel
import com.learning.e_store.viewmodel.AuthUiState
import com.learning.e_store.viewmodel.UiEvent

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange:(String) -> Unit,
    onLoginClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .imePadding()
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello There!",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Log in to your account",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 22.sp
            )
        )

        Image(
            painter = painterResource(R.drawable.welcome_banner),
            contentDescription = "welcome banner"
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { onLoginClick() }
            )
        )

        Spacer(Modifier.height(30.dp))




        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            enabled = !uiState.isLoading // Disable button while loading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login", fontSize = 22.sp)
            }
        }


        uiState.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }
        
    }
}

@Composable
fun LoginRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

) {
    val uiState by viewModel.uiState.collectAsState()
    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login
    )

    NavigateAuth(
        viewModel = viewModel,
        navController = navController,
        snackbarHostState = snackbarHostState
    )
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        uiState = AuthUiState(
            email = "demo@email.com",
            password = "password123",
            isLoading = false,
            error = null
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}
