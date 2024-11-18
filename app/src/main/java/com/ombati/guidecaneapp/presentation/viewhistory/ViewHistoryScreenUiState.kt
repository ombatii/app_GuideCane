package com.ombati.guidecaneapp.presentation.viewhistory

import com.ombati.guidecaneapp.data.model.History

data class ViewHistoryScreenUiState(
    val isLoading: Boolean = false,
    val historyList: List<History> = emptyList(),
    val errorMessage: String? = null
)

