package com.ombati.guidecaneapp.presentation.viewhistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewHistoryViewModel @Inject constructor(
    private val guideCaneRepository: GuideCaneRepository
) : ViewModel() {
    private val _state: MutableStateFlow<ViewHistoryScreenUiState> = MutableStateFlow(ViewHistoryScreenUiState())
    val state: StateFlow<ViewHistoryScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<ViewHistoryScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()


    init {

    }

    fun sendEvent(event: ViewHistoryScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> ViewHistoryScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: ViewHistoryScreenUiState) {
        _state.value = newState
    }

    private fun reduce(oldState: ViewHistoryScreenUiState, event: ViewHistoryScreenUiEvent) {
        when (event) {
            is ViewHistoryScreenUiEvent.GetLocationHistory -> {
                getLocationHistory(oldState, event.smartCaneId)
            }

            ViewHistoryScreenUiEvent.NavigateBackToProfile -> {
                setEffect { ViewHistoryScreenSideEffects.NavigateBackToProfile }
            }

            is ViewHistoryScreenUiEvent.SetLocationHistory -> {
                setState(oldState.copy(historyList = event.locationHistory))
            }

            is ViewHistoryScreenUiEvent.ShowToast -> {
                setEffect { ViewHistoryScreenSideEffects.ShowToast(message = event.message) }
            }

            else -> {

            }
        }
    }

    private fun getLocationHistory(oldState: ViewHistoryScreenUiState, smartCaneId: String) {
        Log.d("getLocationHistory", "Loading history for smartCaneId: $smartCaneId")
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = guideCaneRepository.fetchUserHistory(smartCaneId = smartCaneId)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage = result.exception.message ?: "An error occurred when fetching history"
                    Log.e("getLocationHistory", "Failed to load history: $errorMessage")
                    setEffect { ViewHistoryScreenSideEffects.ShowToast(message = errorMessage) }
                }

                is Result.Success -> {
                    Log.d("getLocationHistory", "Successfully loaded ${result.data.size} history items.")


                    val sortedHistoryList = result.data.sortedByDescending { it.date }

                    setState(oldState.copy(isLoading = false, historyList = sortedHistoryList))
                }
            }
        }
    }

}


