package com.ombati.guidecaneapp.pages

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.res.stringResource
import com.ombati.guidecaneapp.R

@Composable
fun NotificationScreen(navController: NavController) {
    var realTimeLocation by remember { mutableStateOf("Unknown") }
    var geofencingStatus by remember { mutableStateOf("No Alerts") }
    var historicalData by remember { mutableStateOf("No Historical Data Available") }
    var batteryStatus by remember { mutableStateOf("100%") }
    var emergencyAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Location Tracking
        Text(text = stringResource(id = R.string.real_time_location))
        Text(
            text = realTimeLocation,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Geofencing Status
        Text(text = stringResource(id = R.string.geofencing_status))
        Text(
            text = geofencingStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Historical Location Data
        Text(text = stringResource(id = R.string.historical_data))
        Text(
            text = historicalData,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Battery Status Monitoring
        Text(text = stringResource(id = R.string.battery_status))
        Text(
            text = batteryStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Emergency Alerts
        if (emergencyAlert) {
            Text(
                text = stringResource(id = R.string.emergency_alert),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Text(
                text = stringResource(id = R.string.no_emergency),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Push buttons to the bottom

        // Action Buttons (if needed)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                // Navigate to ViewDetailsScreen
                navController.navigate("view_details")
            }) {
                Text(stringResource(id = R.string.view_details))
            }
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    }
}