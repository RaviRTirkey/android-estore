package com.learning.e_store.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.learning.e_store.R

@Composable
fun AuthScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.e_store_icon),
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "Start your shopping journey now",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(20.dp))
        
        Text(
            text = "Best e-store platform with best prices",
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("login")
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)) {
            Text("Login", fontSize = 22.sp)
        }
        
        Spacer(Modifier.height(20.dp))

        OutlinedButton(onClick = {
            navController.navigate("signup")
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)) {
            Text("Signup", fontSize = 22.sp)
        }
    }
}

@Composable
fun AuthRoute(
    navController: NavHostController
) {
    AuthScreen(navController)
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview(){
    // This provides a dummy controller that allows the preview to render
    val navController = rememberNavController()
    AuthScreen(navController = navController)
}