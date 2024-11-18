package com.ombati.guidecaneapp.presentation.adduser

sealed class AddUserScreenUiEvent {
    data class SmartCaneIdChanged(val smartCaneId: String) : AddUserScreenUiEvent()
    data class SecurityKeyChanged(val securityKey: String) : AddUserScreenUiEvent()
    data object AddUserScreenUi : AddUserScreenUiEvent()
    data object Cancel : AddUserScreenUiEvent()


}



