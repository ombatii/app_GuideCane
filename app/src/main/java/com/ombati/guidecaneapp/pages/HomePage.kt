package com.ombati.guidecaneapp.pages

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ombati.guidecaneapp.nav.AppBottomNavigation
import com.ombati.guidecaneapp.nav.NavItem
import com.ombati.guidecaneapp.viewmodel.AuthState
import com.ombati.guidecaneapp.viewmodel.AuthViewModel


private const val TAG = "DeKUTMapActivity"

@Composable
fun HomePage(
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val navController = rememberNavController()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavigation(navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(1.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavItem.Home.route
                ) {
                    composable(NavItem.Home.route) {
                        DeKUTMapComposable()
                    }
                    composable(NavItem.Profile.route) {
                        ProfileScreen(navController = navController)
                    }
                    composable(NavItem.Notification.route) {
                        NotificationScreen(navController = navController)
                    }
                    composable(NavItem.Settings.route) {
                        SettingsScreen(navController = navController, authViewModel = authViewModel)
                    }
                    composable("add_user") {
                        AddUserScreen(navController = navController)
                    }
                    // Add route for ViewDetailsScreen
                    composable("view_details") {
                        ViewDetailsScreen(navController = navController)
                    }
                }
            }
        }
    )
}


@Composable
fun DeKUTMapComposable() {
    // DeKUT Location
    val deKutLocation = LatLng(-0.3983068, 36.9612238)
    val dekutState = rememberMarkerState(position = deKutLocation)

    // Set camera position to focus on DeKUT
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deKutLocation, 15f)
    }
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = true)) }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        //.systemBarsPadding(),
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onPOIClick = {
                Log.d(TAG, "POI clicked: ${it.name}")
            }
        ) {
            val markerClick: (Marker) -> Boolean = {
                Log.d(TAG, "${it.title} was clicked")
                cameraPositionState.projection?.let { projection ->
                    Log.d(TAG, "The current projection is: $projection")
                }
                false
            }

            Marker(
                state = dekutState,
                title = "Dedan Kimathi University of Technology",
                onClick = markerClick
            )
        }
    }
}