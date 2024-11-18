package com.ombati.guidecaneapp.presentation.viewhistory

sealed class ViewHistoryScreenSideEffects {
    data class ShowToast(val message: String) : ViewHistoryScreenSideEffects()
    object NavigateBackToProfile : ViewHistoryScreenSideEffects()
}
