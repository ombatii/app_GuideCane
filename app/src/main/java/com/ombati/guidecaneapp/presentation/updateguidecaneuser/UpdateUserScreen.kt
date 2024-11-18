package com.ombati.guidecaneapp.presentation.updateguidecaneuser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ombati.guidecaneapp.data.model.EmergencyStatus
import com.ombati.guidecaneapp.presentation.side_effects.GuideCaneSideEffects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserScreen(navController: NavController, smartCaneId: String) {
    val viewModel: UpdateUserViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(smartCaneId) {
        viewModel.fetchGuideCaneUserData(smartCaneId)
    }

    var geoFencingLatitude by remember { mutableStateOf(state.geoFencingLatitude?.toString() ?: "") }
    var geoFencingLongitude by remember { mutableStateOf(state.geoFencingLongitude?.toString() ?: "") }
    var caregiverNumber by remember { mutableStateOf(state.caregiverNumber) }
    var emergencyStatus by remember { mutableStateOf(state.emergencyStatus) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GuideCaneSideEffects.ShowSnackBarMessage -> {
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is GuideCaneSideEffects.NavigateToUpdateUser -> {
                    navController.popBackStack()
                }
                is GuideCaneSideEffects.NavigateToAddUser -> {
                    navController.navigate("add_user")
                }
                is GuideCaneSideEffects.NavigateToProfile -> {
                    navController.navigate("profile")
                }
                is GuideCaneSideEffects.NavigateToViewHistory -> {
                    navController.navigate("view_history")
                }
                else -> { }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(title = { Text(text = "Update User") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = geoFencingLatitude,
                    onValueChange = { geoFencingLatitude = it },
                    label = { Text("Geo-Fencing Latitude") },
                    placeholder = { Text(text = "${state.geoFencingLatitude}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = geoFencingLongitude,
                    onValueChange = { geoFencingLongitude = it },
                    label = { Text("Geo-Fencing Longitude") },
                    placeholder = { Text(text = "${state.geoFencingLongitude}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = caregiverNumber,
                    onValueChange = { caregiverNumber = it },
                    label = { Text("Caregiver Number") },
                    placeholder = { Text(text = "${state.caregiverNumber}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Text(text = "Emergency Status")
                Box {
                    Button(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                        Text(text = "${state.emergencyStatus}")
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        EmergencyStatus.values().forEach { status ->
                            DropdownMenuItem(
                                onClick = {
                                    emergencyStatus = status
                                    isDropdownExpanded = false
                                },
                                text = {
                                    Text(text = status.name)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val updates = mapOf(
                            "geo_fen_lat" to geoFencingLatitude.toDoubleOrNull(),
                            "geo_fen_lon" to geoFencingLongitude.toDoubleOrNull(),
                            "caregiver_number" to caregiverNumber,
                            "emergency_status" to emergencyStatus.name
                        ).filterValues { it != null } as Map<String, Any>

                        viewModel.sendEvent(UpdateUserScreenUiEvent.UpdateGuideCaneUser(smartCaneId, updates))
                    }
                ) {
                    Text(text = "Update User")
                }
            }
        }
    )
}
