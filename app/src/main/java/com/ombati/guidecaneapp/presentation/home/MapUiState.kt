package com.ombati.guidecaneapp.presentation.home


data class MapUiState(
    val guideCaneUsers: List<GuideCaneUserWithUiState> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val selectedMarkerId: String? = null
)