package com.ombati.guidecaneapp.presentation.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.ombati.guidecaneapp.R
import com.ombati.guidecaneapp.data.model.GuideCaneUser
import com.ombati.guidecaneapp.presentation.profile.LoadingIndicator
import com.ombati.guidecaneapp.presentation.profile.NoUsersFound

@Composable
fun Map(
    navController: NavController,
    mapViewModel: MapViewModel = hiltViewModel(),
    selectedSmartCaneId: String? = null
) {
    val state = mapViewModel.state.collectAsState().value
    val context = LocalContext.current
    var selectedUser by remember { mutableStateOf<GuideCaneUserWithUiState?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            state.guideCaneUsers.firstOrNull { it.guideCaneUser.id == selectedSmartCaneId }?.let {
                LatLng(it.guideCaneUser.latitude, it.guideCaneUser.longitude)
            } ?: LatLng(-0.3921761, 36.9654067),
            15f
        )
    }

    LaunchedEffect(state.guideCaneUsers) {
        val userLocation = state.guideCaneUsers.firstOrNull { it.guideCaneUser.id == selectedSmartCaneId }
        if (userLocation != null) {
            val targetLocation = LatLng(
                userLocation.guideCaneUser.latitude,
                userLocation.guideCaneUser.longitude
            )
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f))
            selectedUser = userLocation
        }
    }

    Scaffold(content = { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> LoadingIndicator()
                state.guideCaneUsers.isEmpty() -> NoUsersFound()
                else -> {
                    GoogleMap(
                        cameraPositionState = cameraPositionState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        state.guideCaneUsers.forEach { user ->
                            if (user.guideCaneUser.latitude != 0.0 && user.guideCaneUser.longitude != 0.0) {
                                val userLocation = LatLng(user.guideCaneUser.latitude, user.guideCaneUser.longitude)
                                val markerState = rememberMarkerState(position = userLocation)

                                MarkerWithInfoWindow(
                                    markerState = markerState,
                                    user = user,
                                    context = context,
                                    onClick = { selectedUser = user }
                                )
                            }
                        }
                    }

                    selectedUser?.let { user ->
                        CustomInfoWindow(user = user.guideCaneUser)
                    }
                }
            }
        }
    })
}


@Composable
fun MarkerWithInfoWindow(
    markerState: MarkerState,
    user: GuideCaneUserWithUiState,
    context: Context,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val finalIcon = bitmapDescriptorFromVector(
        context,
        R.drawable.marker_blinking,
        user.iconColor.copy(alpha = if (user.isOutOfGeofence) blinkAlpha else 1f)
    )

    Marker(
        state = markerState,
        title = user.guideCaneUser.smartCaneId,
        snippet = "Emergency Status: ${user.guideCaneUser.emergencyStatus}",
        icon = finalIcon,
        onClick = {
            onClick()
            true
        }
    )
}

@Composable
fun CustomInfoWindow(
    user: GuideCaneUser
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .border(1.dp, Color.Black)
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = "Smart Cane ID: ${user.smartCaneId}",
                style = TextStyle(fontSize = 16.sp, color = Color.Black)
            )
            Text(
                text = "Emergency Status: ${user.emergencyStatus}",
                style = TextStyle(fontSize = 14.sp, color = Color.Red)
            )
        }
    }
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    color: Color
): BitmapDescriptor {
    val vectorDrawable = context.getDrawable(vectorResId)
    vectorDrawable?.setTint(color.toArgb())

    val bitmap = Bitmap.createBitmap(
        vectorDrawable!!.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}