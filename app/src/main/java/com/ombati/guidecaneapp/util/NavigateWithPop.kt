package com.ombati.guidecaneapp.util


import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination


fun NavController.navigateWithPop(route : String){
    navigate(route){
        popUpTo(graph.findStartDestination().id){
            saveState = true
        }
        restoreState = true
    }
}