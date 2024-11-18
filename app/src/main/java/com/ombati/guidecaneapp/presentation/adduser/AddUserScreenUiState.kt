package com.ombati.guidecaneapp.presentation.adduser


data class AddUserScreenUiState(
    val isLoading: Boolean = false,
    val smartCaneId: String = "",
    val appUserId: String = "",
    val securityKey: String = "",
    val errorMessage: String? = null,
    val isUserAdded: Boolean = false,
    val isAddUserDialogVisible: Boolean = false
)

