package com.ombati.guidecaneapp.presentation.profile


import com.ombati.guidecaneapp.data.model.GuideCaneUser
import com.ombati.guidecaneapp.data.model.History

sealed class ProfileScreenUiEvent {
    data class GetGuideCaneUsers(val appUserId: String) : ProfileScreenUiEvent()

    data class UpdateGuideCaneUser(
        val smartCaneId: String,
        val updates: Map<String, Any>
    ) : ProfileScreenUiEvent()

    data class GetAppUserDetails(val appUserId: String) : ProfileScreenUiEvent()

    data class AddGuideCaneUser(val appUserId: String, val smartCaneId: String, val securityKey: String) : ProfileScreenUiEvent()


    data class GetUserHistory(val smartCaneId: String) : ProfileScreenUiEvent()

    data class DeleteGuideCaneUser(val smartCaneId: String) : ProfileScreenUiEvent()

    object NavigateToUpdateUser : ProfileScreenUiEvent()

    object NavigateToViewHistory : ProfileScreenUiEvent()

    data class OnChangeAddUserDialogState(val show: Boolean) : ProfileScreenUiEvent()

    data class OnChangeUpdateUserDialogState(val show: Boolean) : ProfileScreenUiEvent()

    data class SetUserToBeUpdated(val guideCaneUser: GuideCaneUser) : ProfileScreenUiEvent()

    data class SetUserHistory(val historyList: List<History>) : ProfileScreenUiEvent()
}
