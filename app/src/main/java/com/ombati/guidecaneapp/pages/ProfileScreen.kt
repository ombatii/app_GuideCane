package com.ombati.guidecaneapp.pages


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navigate to the AddUserScreen when the button is clicked
                navController.navigate("add_user")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Repeating the card for default values
                items(2) {
                    ProfileCard()
                }
            }
        }
    )
}


@Composable
fun ProfileCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp) // Material3 elevation style
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Name of the person
            Text(
                text = "Brian",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Location (Latitude, Longitude)
            Text(
                text = "Latitude: -1.286389, Longitude: 36.817223",
                fontSize = 14.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Battery Status with ProgressBar
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Battery Status: 56%")
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = 0.56f,
                    modifier = Modifier
                        .height(8.dp)
                        .width(150.dp),
                    color = Color.Green
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Buttons: Update and Delete
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { /* Update action */ }) {
                    Text(text = "Update")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Delete action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // containerColor replaces backgroundColor
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}