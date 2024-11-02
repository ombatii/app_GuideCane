package com.ombati.guidecaneapp.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ombati.guidecaneapp.pages.HomePage
import com.ombati.guidecaneapp.pages.LoginPage
import com.ombati.guidecaneapp.pages.SignupPage


@Composable
fun MyAppNavigation(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState(AuthState.Loading)


    when (authState) {
        is AuthState.Loading -> {
            SplashScreen(modifier = modifier)
        }
        is AuthState.Authenticated -> {
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
        }
        is AuthState.Unauthenticated -> {
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
        is AuthState.Error -> {
            ErrorScreen(modifier = modifier, message = (authState as AuthState.Error).message)
        }
    }
}

@Composable
fun SplashScreen(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


@Composable
fun ErrorScreen(modifier: Modifier, message: String) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $message")
    }
}