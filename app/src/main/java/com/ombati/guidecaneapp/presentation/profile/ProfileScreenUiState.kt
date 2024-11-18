package com.ombati.guidecaneapp.presentation.profile

import com.ombati.guidecaneapp.data.model.AppUser
import com.ombati.guidecaneapp.data.model.History
import com.ombati.guidecaneapp.data.model.GuideCaneUser


data class ProfileScreenUiState(
    val isLoading: Boolean = false,
    val appUser: AppUser? = null,
    val guideCaneUsers: List<GuideCaneUser> = emptyList(),
    val userHistory: List<History> = emptyList(),
    val isShowAddUserDialog: Boolean = false,
    val isShowUpdateUserDialog: Boolean = false,
    val userToBeUpdated: GuideCaneUser? = null
)
