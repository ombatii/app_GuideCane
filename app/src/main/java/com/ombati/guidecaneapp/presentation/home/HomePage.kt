package com.ombati.guidecaneapp.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ombati.guidecaneapp.nav.AppBottomNavigation
import com.ombati.guidecaneapp.nav.NavItem
import com.ombati.guidecaneapp.pages.NotificationScreen
import com.ombati.guidecaneapp.pages.SettingsScreen
import com.ombati.guidecaneapp.presentation.adduser.AddUserScreen
import com.ombati.guidecaneapp.presentation.profile.ProfileScreen
import com.ombati.guidecaneapp.presentation.updateguidecaneuser.UpdateUserScreen
import com.ombati.guidecaneapp.presentation.viewhistory.ViewHistoryScreen
import com.ombati.guidecaneapp.viewmodel.AuthState
import com.ombati.guidecaneapp.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
                        Map(navController = navController)
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
                    composable("view_history/{smartCaneId}") { backStackEntry ->
                        val smartCaneId = backStackEntry.arguments?.getString("smartCaneId") ?: ""
                        if (smartCaneId.isNotEmpty()) {
                            ViewHistoryScreen(navController = navController, smartCaneId = smartCaneId)
                        }
                    }
                    composable("update_user/{smartCaneId}") { backStackEntry ->
                        val smartCaneId = backStackEntry.arguments?.getString("smartCaneId") ?: ""
                        if (smartCaneId.isNotEmpty()) {
                            UpdateUserScreen(navController = navController, smartCaneId = smartCaneId)
                        }
                    }
                    composable("map/{smartCaneId}") { backStackEntry ->
                        val smartCaneId = backStackEntry.arguments?.getString("smartCaneId") ?: ""
                        if (smartCaneId.isNotEmpty()) {
                            Map(navController = navController, selectedSmartCaneId = smartCaneId)
                        }
                    }
                }
            }
        }
    )
}
