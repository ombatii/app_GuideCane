package com.ombati.guidecaneapp.presentation.viewhistory

import com.ombati.guidecaneapp.data.model.History

sealed class ViewHistoryScreenUiEvent {
    data class GetLocationHistory(val smartCaneId: String) : ViewHistoryScreenUiEvent()

    data class SetLocationHistory(val locationHistory: List<History>) : ViewHistoryScreenUiEvent()

    object NavigateBackToProfile : ViewHistoryScreenUiEvent()

    data class ShowToast(val message: String) : ViewHistoryScreenUiEvent()
}
