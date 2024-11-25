package com.ombati.guidecaneapp


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ombati.guidecaneapp.ui.theme.GuideCaneAppTheme
import com.ombati.guidecaneapp.viewmodel.AuthViewModel
import com.ombati.guidecaneapp.viewmodel.MyAppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val authViewModel: AuthViewModel by viewModels()
        //NotificationWorker.startSyncing(this)
        //CheckBatteryStatusWorker.startSyncing(this)
        //CheckEmergencyStatusWorker.startSyncing(this)
        //CheckLocationProximityWorker.startSyncing(this)



        setContent {
            GuideCaneAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize())
                    {
                    MyAppNavigation(
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}