package com.ombati.guidecaneapp.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery3Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ombati.guidecaneapp.data.model.GuideCaneUser
import com.ombati.guidecaneapp.presentation.profile.ProfileScreenUiEvent
import com.ombati.guidecaneapp.presentation.profile.ProfileViewModel


@Composable
fun ProfileCard(
    navController: NavController,
    guideCaneUser: GuideCaneUser,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val emergencyColor = if (guideCaneUser.emergencyStatus == "Alert") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
    val batteryLevel = guideCaneUser.batteryLevel?.toIntOrNull() ?: 50

    val batteryColor = if (batteryLevel < 16) MaterialTheme.colorScheme.error else Color.Green
    val batteryIcon = when {
        batteryLevel > 75 -> Icons.Default.BatteryFull
        batteryLevel > 50 -> Icons.Default.Battery3Bar
        batteryLevel > 15 -> Icons.Default.Battery6Bar
        else -> Icons.Default.BatteryAlert
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
          //.height(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            //containerColor = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // GuideCane ID and Battery Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${guideCaneUser.id}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = batteryIcon,
                        contentDescription = "Battery Icon",
                        tint = batteryColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${guideCaneUser.batteryLevel}%",
                        color = batteryColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PinDrop,
                        contentDescription = "Location Icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current Location",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${guideCaneUser.latitude}, ${guideCaneUser.longitude}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MapsHomeWork,
                        contentDescription = "Geo-Fencing Icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Geo-fencing Point",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${guideCaneUser.geoFencingLatitude}, ${guideCaneUser.geoFencingLongitude}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = "Emergency Contact Icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Caregiver number",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${guideCaneUser.caregiverNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency Status Icon",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Status",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${guideCaneUser.emergencyStatus}",
                        style = MaterialTheme.typography.labelSmall,
                        color = emergencyColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { navController.navigate("map/${guideCaneUser.id}") }) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "View Location"
                    )
                }
                IconButton(onClick = { navController.navigate("view_history/${guideCaneUser.id}") }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "View History"
                    )
                }
                IconButton(onClick = {
                    navController.navigate("update_user/${guideCaneUser.id}")
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Update User"
                    )
                }
                IconButton(onClick = {
                    val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (appUserId.isEmpty()) {
                        Toast.makeText(context, "User is not authenticated", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.sendEvent(ProfileScreenUiEvent.DeleteGuideCaneUser(guideCaneUser.id))
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete User"
                    )
                }
            }
        }
    }
}