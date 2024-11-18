package com.ombati.guidecaneapp.presentation.updateguidecaneuser

import com.ombati.guidecaneapp.data.model.EmergencyStatus

sealed class UpdateUserScreenUiEvent {
    data class GetGuideCaneUser(val appUserId: String) : UpdateUserScreenUiEvent()
    data class UpdateGuideCaneUser(val smartCaneId: String, val updates: Map<String, Any>) : UpdateUserScreenUiEvent()
    data class OnChangeGeoFencing(val geoFencing: String) : UpdateUserScreenUiEvent()
    data class OnChangeEmergencyStatus(val emergencyStatus: EmergencyStatus) : UpdateUserScreenUiEvent()
    data class OnChangeCaregiverNumber(val caregiverNumber: String) : UpdateUserScreenUiEvent()
    object NavigateBackToProfile : UpdateUserScreenUiEvent()
    data class ShowToast(val message: String) : UpdateUserScreenUiEvent()
}

