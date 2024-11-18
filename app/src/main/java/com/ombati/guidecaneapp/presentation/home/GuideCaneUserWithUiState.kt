package com.ombati.guidecaneapp.presentation.home

import androidx.compose.ui.graphics.Color
import com.ombati.guidecaneapp.data.model.GuideCaneUser

data class GuideCaneUserWithUiState(
    val guideCaneUser: GuideCaneUser,
    val isOutOfGeofence: Boolean,
    val iconColor: Color
)
