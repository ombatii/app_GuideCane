package com.ombati.guidecaneapp.presentation.updateguidecaneuser

import com.ombati.guidecaneapp.data.model.EmergencyStatus
import com.ombati.guidecaneapp.data.model.GuideCaneUser


data class UpdateUserScreenState(
    val isLoading: Boolean = false,
    val guideCaneUser: GuideCaneUser? = null,
    val errorMessage: String? = null,
    val geoFencing: String = "",
    val geoFencingLatitude: Double? = null,
    val geoFencingLongitude: Double? = null,
    val emergencyStatus: EmergencyStatus = EmergencyStatus.Normal,
    val caregiverNumber: String = ""
)




