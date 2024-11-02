package com.ombati.guidecaneapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddUserScreen(navController: NavController) {
    var userID by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    // A list to hold multiple geo-fencing data entries
    var geoFencingList by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }

    // List to hold sets of longitude and latitude for geo-fencing
    var geoFencingPoints by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }

    // Inputs for longitude and latitude
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Center the text "Adding new user", make it bold, and increase the font size
        Text(
            text = "Adding new user",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = androidx.compose.ui.Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ID input
        OutlinedTextField(
            value = userID,
            onValueChange = { userID = it },
            label = { Text("ID") },
            modifier = Modifier.fillMaxWidth()
        )

        // Name input
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("NAME") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Geo-fencing inputs
        Text(text = "Geo-Fencing Points (Input 4 Points)", style = MaterialTheme.typography.bodyMedium)

        // Input fields for longitude and latitude
        repeat(4) { index ->
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = geoFencingPoints.getOrElse(index) { Pair("", "") }.first,
                    onValueChange = {
                        if (index < geoFencingPoints.size) {
                            geoFencingPoints[index] = Pair(it, geoFencingPoints.getOrElse(index) { Pair("", "") }.second)
                        } else {
                            geoFencingPoints.add(Pair(it, geoFencingPoints.getOrElse(index) { Pair("", "") }.second))
                        }
                    },
                    label = { Text("Longitude $index") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = geoFencingPoints.getOrElse(index) { Pair("", "") }.second,
                    onValueChange = {
                        if (index < geoFencingPoints.size) {
                            geoFencingPoints[index] = Pair(geoFencingPoints.getOrElse(index) { Pair("", "") }.first, it)
                        } else {
                            geoFencingPoints.add(Pair(geoFencingPoints.getOrElse(index) { Pair("", "") }.first, it))
                        }
                    },
                    label = { Text("Latitude $index") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to add geo-fencing coordinates
        Button(
            onClick = {
                // Add all the entered geo-fencing points to the list
                geoFencingList.addAll(geoFencingPoints)
                geoFencingPoints.clear() // Clear the points after adding
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Geo-Fencing Points")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display added Geo-Fencing entries
        Column {
            geoFencingList.forEach { geo ->
                Text(text = "Geo-Fencing: Longitude = ${geo.first}, Latitude = ${geo.second}")
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push buttons to the bottom

        // Save and Cancel buttons in a row
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                // Handle save action here (e.g., save to database)
                // Then navigate back to the profile screen
                navController.popBackStack() // Navigate back to profile
            }) {
                Text("Save")
            }

            Button(onClick = {
                // Navigate back to the profile screen without saving
                navController.popBackStack()
            }) {
                Text("Cancel")
            }
        }
    }
}