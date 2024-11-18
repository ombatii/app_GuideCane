package com.ombati.guidecaneapp.presentation.home

sealed class MapSideEffects {
    data class ShowToast(val message: String) : MapSideEffects()
}