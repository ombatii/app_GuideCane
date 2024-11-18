package com.ombati.guidecaneapp.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ombati.guidecaneapp.presentation.home.HomePage
import com.ombati.guidecaneapp.pages.LoginPage
import com.ombati.guidecaneapp.pages.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState(AuthState.Loading)

    if (authState is AuthState.Authenticated) {
        NavHost(navController = navController, startDestination = "home") {
            composable("login") {
                LoginPage(modifier = modifier, navController = navController, authViewModel = authViewModel)
            }
            composable("signup") {
                SignupPage(modifier = modifier, navController = navController, authViewModel = authViewModel)
            }
            composable("home") {
                HomePage(authViewModel = authViewModel)
            }
        }
    } else {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginPage(modifier = modifier, navController = navController, authViewModel = authViewModel)
            }
            composable("signup") {
                SignupPage(modifier = modifier, navController = navController, authViewModel = authViewModel)
            }
            composable("home") {
                HomePage(authViewModel = authViewModel)
            }
        }
    }
}
