package com.ombati.guidecaneapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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


        Text(text = stringResource(id = R.string.real_time_location))
        Text(
            text = realTimeLocation,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )


        Text(text = stringResource(id = R.string.geofencing_status))
        Text(
            text = geofencingStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )


        Text(text = stringResource(id = R.string.historical_data))
        Text(
            text = historicalData,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(text = stringResource(id = R.string.battery_status))
        Text(
            text = batteryStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

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

        Spacer(modifier = Modifier.weight(1f))


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
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