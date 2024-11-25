package com.ombati.guidecaneapp.presentation.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val guideCaneRepository: GuideCaneRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState(isLoading = true))
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    private val _effect = Channel<MapSideEffects>()
    val effect = _effect.receiveAsFlow()

    init {
        loadGuideCaneUsers()
    }

    fun handleEvent(event: MapUiEvent) {
        when (event) {
            is MapUiEvent.LoadGuideCaneUsers -> loadGuideCaneUsers()
            is MapUiEvent.MarkerClicked -> {
                Log.d("MapViewModel", "Marker clicked for ${event.smartCaneId}")
            }
        }
    }

    private fun loadGuideCaneUsers() {
        val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (appUserId.isEmpty()) {
            viewModelScope.launch {
                _effect.send(MapSideEffects.ShowToast("No user ID found. Please log in."))
            }
            _state.update { it.copy(isLoading = false) }
            return
        }

        _state.update { it.copy(isLoading = true) }

        guideCaneRepository.fetchGuideCaneUsersForCurrentUser(
            appUserId = appUserId,
            onGuideCaneUsersUpdated = { result ->
                viewModelScope.launch {
                    when (result) {
                        is Result.Success -> {
                            val users = result.data.filter { user ->
                                user.latitude != 0.0 && user.longitude != 0.0
                            }.map { user ->
                                val isOutOfGeofence = if (user.geoFencingLatitude != 0.0 && user.geoFencingLongitude != 0.0) {
                                    calculateDistance(
                                        user.latitude,
                                        user.longitude,
                                        user.geoFencingLatitude,
                                        user.geoFencingLongitude
                                    ) > 50
                                } else {
                                    false
                                }

                                GuideCaneUserWithUiState(
                                    guideCaneUser = user,
                                    isOutOfGeofence = isOutOfGeofence,
                                    iconColor = if (isOutOfGeofence || user.emergencyStatus == "Alert") Color.Red else Color.Blue
                                )
                            }

                            _state.update {
                                it.copy(
                                    guideCaneUsers = users,
                                    isLoading = false
                                )
                            }
                        }
                        is Result.Failure -> {
                            Log.e("MapViewModel", "Error: ${result.exception.message}")
                            _state.update { it.copy(isLoading = false) }
                            _effect.send(
                                MapSideEffects.ShowToast("Failed to load GuideCane users: ${result.exception.message}")
                            )
                        }
                        else -> {}
                    }
                }
            }
        )
    }
}
