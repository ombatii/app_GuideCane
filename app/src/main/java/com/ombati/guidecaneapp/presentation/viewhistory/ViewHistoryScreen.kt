package com.ombati.guidecaneapp.presentation.viewhistory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ombati.guidecaneapp.data.model.History
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewHistoryScreen(navController: NavController, smartCaneId: String) {
    Log.d("ViewHistoryScreen", "Entering ViewHistoryScreen with smartCaneId: $smartCaneId")
    val viewModel: ViewHistoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = "VIEW_HISTORY_SCREEN") {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is ViewHistoryScreenSideEffects.ShowToast -> {
                    snackBarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                        actionLabel = "DISMISS"
                    )
                }
                else -> {}
            }
        }.collect()
    }


    LaunchedEffect(smartCaneId) {
        if (smartCaneId.isNotEmpty()) {
            viewModel.sendEvent(ViewHistoryScreenUiEvent.GetLocationHistory(smartCaneId))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "User History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("profile") }) {
                Text("Back")
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Location History Details",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "History of User's Locations:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.historyList) { location ->
                            LocationItem(location = location)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationItem(location: History) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Log.d("LocationItem", "Displaying location: $location")

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Date: ${location.date}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Latitude: ${location.latitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Longitude: ${location.longitude}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
