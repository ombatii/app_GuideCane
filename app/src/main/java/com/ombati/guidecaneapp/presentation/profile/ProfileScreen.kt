package com.ombati.guidecaneapp.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ombati.guidecaneapp.data.model.GuideCaneUser


@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_user") }) {
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
                when {
                    state.isLoading -> {
                        item {
                            LoadingIndicator()
                        }
                    }
                    state.guideCaneUsers.isEmpty() -> {
                        item {
                            NoUsersFound()
                        }
                    }
                    else -> {
                        items(state.guideCaneUsers) { user ->
                            ProfileCard(navController = navController, guideCaneUser = user, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoUsersFound() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No users found.",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileCard(
    navController: NavController,
    guideCaneUser: GuideCaneUser,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val emergencyColor = if (guideCaneUser.emergencyStatus == "Alert") Color.Red else Color.Green

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("User ID: ${guideCaneUser.id}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Latitude: ${guideCaneUser.latitude}, Longitude: ${guideCaneUser.longitude}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Battery Level: ${guideCaneUser.batteryLevel}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Emergency Status: ${guideCaneUser.emergencyStatus}",
                fontSize = 16.sp,
                color = emergencyColor
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    navController.navigate("update_user/${guideCaneUser.id}")
                }) {
                    Text(text = "Update")
                }

                Button(onClick = {
                    val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (appUserId.isEmpty()) {
                        Toast.makeText(context, "User is not authenticated", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.sendEvent(ProfileScreenUiEvent.DeleteGuideCaneUser(guideCaneUser.id))
                    }
                }) {
                    Text("Delete")
                }

                Button(onClick = {
                    navController.navigate("view_history/${guideCaneUser.id}")
                }) {
                    Text(text = "History")
                }

                Button(onClick = {
                    navController.navigate("map/${guideCaneUser.id}")
                }) {
                    Text(text = "Location")
                }
            }
        }
    }
}
