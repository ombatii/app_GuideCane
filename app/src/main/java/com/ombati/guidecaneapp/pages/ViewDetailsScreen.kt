package com.ombati.guidecaneapp.pages

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ombati.guidecaneapp.R

@Composable
fun ViewDetailsScreen(navController: NavController) {
    // Sample data
    val locationHistoryList = List(20) { "Location ${it + 1}: Latitude = ${50 + it}, Longitude = ${100 + it}" }
    val geofencingHistoryList = List(20) { "Geofence crossed at Latitude: ${50 + it}, Longitude: ${100 + it}" }
    val batteryHistoryList = List(20) { "Battery Status: ${100 - it}% at Time: ${it + 1}:00 PM" }
    val emergencyHistoryList = List(20) { if (it % 2 == 0) "Emergency Alert at ${it + 1}:00 PM" else "No Emergency" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = stringResource(id = R.string.details_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Location History
        Text(text = stringResource(id = R.string.location_history))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(locationHistoryList) { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Geofencing History
        Text(text = stringResource(id = R.string.geofencing_history))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(geofencingHistoryList) { geofence ->
                Text(
                    text = geofence,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery History
        Text(text = stringResource(id = R.string.battery_history))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(batteryHistoryList) { batteryStatus ->
                Text(
                    text = batteryStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Alerts History
        Text(text = stringResource(id = R.string.emergency_alert_history))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(emergencyHistoryList) { emergency ->
                Text(
                    text = emergency,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push buttons to the bottom

        // Dismiss Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.dismiss))
        }
    }
}