package com.ombati.guidecaneapp.presentation.updateguidecaneuser


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.model.EmergencyStatus
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import com.ombati.guidecaneapp.presentation.side_effects.GuideCaneSideEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val guideCaneRepository: GuideCaneRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UpdateUserScreenState())
    val state: StateFlow<UpdateUserScreenState> = _state.asStateFlow()

    private val _effect = Channel<GuideCaneSideEffects>()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(UpdateUserScreenUiEvent.GetGuideCaneUser(appUserId = "defaultAppUserId"))
    }

    fun sendEvent(event: UpdateUserScreenUiEvent) {
        reduce(oldState = _state.value, event = event)
    }

    private fun setEffect(builder: () -> GuideCaneSideEffects) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun setState(newState: UpdateUserScreenState) {
        _state.value = newState
    }

    private fun reduce(oldState: UpdateUserScreenState, event: UpdateUserScreenUiEvent) {
        when (event) {

            is UpdateUserScreenUiEvent.UpdateGuideCaneUser -> {
                updateGuideCaneUser(oldState = oldState, smartCaneId = event.smartCaneId, updates = event.updates)
            }
            is UpdateUserScreenUiEvent.OnChangeGeoFencing -> {
                setState(oldState.copy(geoFencing = event.geoFencing))
            }
            is UpdateUserScreenUiEvent.OnChangeEmergencyStatus -> {
                setState(oldState.copy(emergencyStatus = event.emergencyStatus))
            }
            is UpdateUserScreenUiEvent.OnChangeCaregiverNumber -> {
                setState(oldState.copy(caregiverNumber = event.caregiverNumber))
            }
            UpdateUserScreenUiEvent.NavigateBackToProfile -> {
                setEffect { GuideCaneSideEffects.NavigateToUpdateUser }
            }
            is UpdateUserScreenUiEvent.ShowToast -> {
                setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = event.message) }
            }

            else -> {}
        }
    }

    fun fetchGuideCaneUserData(smartCaneId: String) {
        viewModelScope.launch {
            when (val result = guideCaneRepository.fetchGuideCaneUser(smartCaneId)) {
                is Result.Success -> {
                    val user = result.data
                    val emergencyStatus = try {
                        EmergencyStatus.valueOf(user.emergencyStatus)
                    } catch (e: IllegalArgumentException) {
                        EmergencyStatus.Normal
                    }
                    setState(
                        UpdateUserScreenState(
                            geoFencingLatitude = user.geoFencingLatitude,
                            geoFencingLongitude = user.geoFencingLongitude,
                            caregiverNumber = user.caregiverNumber,
                            emergencyStatus = emergencyStatus
                        )
                    )
                }
                is Result.Failure -> {
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage("Error loading user data") }
                }

                else -> {}
            }
        }
    }




    private fun updateGuideCaneUser(oldState: UpdateUserScreenState, smartCaneId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result = guideCaneRepository.updateGuideCaneUser(smartCaneId, updates)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "Failed to update user data"
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }
                is Result.Success -> {
                    setState(oldState.copy(isLoading = false))
                    setEffect { GuideCaneSideEffects.ShowSnackBarMessage(message = "User data updated successfully") }
                    setEffect { GuideCaneSideEffects.NavigateToUpdateUser }
                }

                else -> {}
            }
        }
    }
}
