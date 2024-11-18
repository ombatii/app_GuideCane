package com.ombati.guidecaneapp.presentation.home

sealed class MapUiEvent {
    object LoadGuideCaneUsers : MapUiEvent()
    data class MarkerClicked(val smartCaneId: String) : MapUiEvent()
}